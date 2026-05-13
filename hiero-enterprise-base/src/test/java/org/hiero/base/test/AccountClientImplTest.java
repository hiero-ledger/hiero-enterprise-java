package org.hiero.base.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.EvmHook;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.HookExtensionPoint;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.util.List;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.HookDetails;
import org.hiero.base.implementation.AccountClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountHookUpdateRequest;
import org.hiero.base.protocol.data.AccountHookUpdateResult;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.hiero.base.protocol.data.AccountUpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

public class AccountClientImplTest {

  private ProtocolLayerClient mockProtocolLayerClient;
  private AccountClientImpl accountClientImpl;

  @BeforeEach
  public void setUp() {
    mockProtocolLayerClient = mock(ProtocolLayerClient.class);
    accountClientImpl = new AccountClientImpl(mockProtocolLayerClient);
  }

  @Test
  public void testGetAccountBalanceValidPositiveBalance() throws HieroException {
    AccountId accountId = AccountId.fromString("0.0.12345");
    Hbar expectedBalance = new Hbar(10);

    // Mock the response
    AccountBalanceResponse mockResponse = mock(AccountBalanceResponse.class);
    when(mockResponse.hbars()).thenReturn(expectedBalance);

    when(mockProtocolLayerClient.executeAccountBalanceQuery(
            ArgumentMatchers.any(AccountBalanceRequest.class)))
        .thenReturn(mockResponse);

    Hbar balance = accountClientImpl.getAccountBalance(accountId);

    assertEquals(expectedBalance, balance);
  }

  @Test
  public void testGetAccountBalanceZeroBalance() throws HieroException {
    AccountId accountId = AccountId.fromString("0.0.67890");
    Hbar expectedBalance = new Hbar(0);

    AccountBalanceResponse mockResponse = mock(AccountBalanceResponse.class);
    when(mockResponse.hbars()).thenReturn(expectedBalance);

    when(mockProtocolLayerClient.executeAccountBalanceQuery(
            ArgumentMatchers.any(AccountBalanceRequest.class)))
        .thenReturn(mockResponse);

    Hbar balance = accountClientImpl.getAccountBalance(accountId);

    assertEquals(expectedBalance, balance);
  }

  @Test
  public void testGetAccountBalanceInvalidAccountThrowsException() throws HieroException {
    AccountId invalidAccountId = AccountId.fromString("0.0.9999999");

    when(mockProtocolLayerClient.executeAccountBalanceQuery(
            ArgumentMatchers.any(AccountBalanceRequest.class)))
        .thenThrow(new HieroException("Invalid account"));

    assertThrows(
        HieroException.class,
        () -> {
          accountClientImpl.getAccountBalance(invalidAccountId);
        });
  }

  @Test
  public void testGetAccountBalanceNullThrowsException() {
    assertThrows(
        NullPointerException.class,
        () -> {
          accountClientImpl.getAccountBalance((AccountId) null);
        });
  }

  @Test
  public void testGetAccountBalanceProtocolLayerClientFails() throws HieroException {
    AccountId accountId = AccountId.fromString("0.0.12345");

    when(mockProtocolLayerClient.executeAccountBalanceQuery(
            ArgumentMatchers.any(AccountBalanceRequest.class)))
        .thenThrow(new RuntimeException("Protocol Layer Failure"));

    assertThrows(
        RuntimeException.class,
        () -> {
          accountClientImpl.getAccountBalance(accountId);
        });
  }

  // tests for createAccount method
  @Test
  void testCreateAccountSuccessful() throws HieroException {
    Hbar initialBalance = Hbar.from(100);

    AccountCreateResult mockResult = mock(AccountCreateResult.class);
    Account mockAccount = mock(Account.class);

    when(mockAccount.accountId()).thenReturn(AccountId.fromString("0.0.12345"));
    when(mockResult.newAccount()).thenReturn(mockAccount);
    when(mockProtocolLayerClient.executeAccountCreateTransaction(any(AccountCreateRequest.class)))
        .thenReturn(mockResult);

    Account result = accountClientImpl.createAccount(initialBalance);

    assertNotNull(result);
    assertEquals(AccountId.fromString("0.0.12345"), result.accountId());
    verify(mockProtocolLayerClient, times(1))
        .executeAccountCreateTransaction(any(AccountCreateRequest.class));
  }

  @Test
  void testCreateAccountInvalidInitialBalanceNull() {
    Hbar initialBalance = null;

    assertThrows(NullPointerException.class, () -> accountClientImpl.createAccount(initialBalance));
  }

  @Test
  void testCreateAccount_invalidInitialBalance_negative() {
    Hbar initialBalance = Hbar.from(-100);
    HieroException exception =
        assertThrows(HieroException.class, () -> accountClientImpl.createAccount(initialBalance));

    assertTrue(exception.getMessage().contains("Invalid initial balance"));
  }

  @Test
  void testCreateAccountHieroExceptionThrown() throws HieroException {
    Hbar initialBalance = Hbar.from(100);

    when(mockProtocolLayerClient.executeAccountCreateTransaction(any(AccountCreateRequest.class)))
        .thenThrow(new HieroException("Transaction failed"));

    Exception exception =
        assertThrows(HieroException.class, () -> accountClientImpl.createAccount(initialBalance));
    assertEquals("Transaction failed", exception.getMessage());
  }

  @Test
  void testUpdateAccountKeySuccessful() throws HieroException {
    Account account = Account.of(AccountId.fromString("0.0.12345"), PrivateKey.generateECDSA());
    PrivateKey updatedPrivateKey = PrivateKey.generateECDSA();
    when(mockProtocolLayerClient.executeAccountUpdateTransaction(any(AccountUpdateRequest.class)))
        .thenReturn(
            new AccountUpdateResult(TransactionId.generate(account.accountId()), Status.SUCCESS));

    Account updatedAccount = accountClientImpl.updateAccountKey(account, updatedPrivateKey);

    assertEquals(account.accountId(), updatedAccount.accountId());
    assertEquals(updatedPrivateKey.getPublicKey(), updatedAccount.publicKey());
    assertEquals(updatedPrivateKey, updatedAccount.privateKey());
    verify(mockProtocolLayerClient, times(1))
        .executeAccountUpdateTransaction(any(AccountUpdateRequest.class));
  }

  @Test
  void testUpdateAccountMemoSuccessful() throws HieroException {
    Account account = Account.of(AccountId.fromString("0.0.12345"), PrivateKey.generateECDSA());
    String memo = "updated-memo";
    ArgumentCaptor<AccountUpdateRequest> requestCaptor =
        ArgumentCaptor.forClass(AccountUpdateRequest.class);
    when(mockProtocolLayerClient.executeAccountUpdateTransaction(any(AccountUpdateRequest.class)))
        .thenReturn(
            new AccountUpdateResult(TransactionId.generate(account.accountId()), Status.SUCCESS));

    accountClientImpl.updateAccountMemo(account, memo);

    verify(mockProtocolLayerClient, times(1))
        .executeAccountUpdateTransaction(requestCaptor.capture());
    AccountUpdateRequest request = requestCaptor.getValue();
    assertEquals(account, request.toUpdate());
    assertEquals(memo, request.memo());
    assertNull(request.updatedPrivateKey());
  }

  @Test
  void testUpdateAccountMemoBlankMemoSuccessful() throws HieroException {
    Account account = Account.of(AccountId.fromString("0.0.12345"), PrivateKey.generateECDSA());
    String memo = " ";
    ArgumentCaptor<AccountUpdateRequest> requestCaptor =
        ArgumentCaptor.forClass(AccountUpdateRequest.class);
    when(mockProtocolLayerClient.executeAccountUpdateTransaction(any(AccountUpdateRequest.class)))
        .thenReturn(
            new AccountUpdateResult(TransactionId.generate(account.accountId()), Status.SUCCESS));

    accountClientImpl.updateAccountMemo(account, memo);

    verify(mockProtocolLayerClient, times(1))
        .executeAccountUpdateTransaction(requestCaptor.capture());
    AccountUpdateRequest request = requestCaptor.getValue();
    assertEquals(account, request.toUpdate());
    assertEquals(memo, request.memo());
    assertNull(request.updatedPrivateKey());
  }

  @Test
  void testUpdateAccountSuccessful() throws HieroException {
    Account account = Account.of(AccountId.fromString("0.0.12345"), PrivateKey.generateECDSA());
    PrivateKey updatedPrivateKey = PrivateKey.generateECDSA();
    String memo = "updated-memo";
    ArgumentCaptor<AccountUpdateRequest> requestCaptor =
        ArgumentCaptor.forClass(AccountUpdateRequest.class);
    when(mockProtocolLayerClient.executeAccountUpdateTransaction(any(AccountUpdateRequest.class)))
        .thenReturn(
            new AccountUpdateResult(TransactionId.generate(account.accountId()), Status.SUCCESS));

    Account updatedAccount = accountClientImpl.updateAccount(account, updatedPrivateKey, memo);

    verify(mockProtocolLayerClient, times(1))
        .executeAccountUpdateTransaction(requestCaptor.capture());
    AccountUpdateRequest request = requestCaptor.getValue();
    assertEquals(account, request.toUpdate());
    assertEquals(updatedPrivateKey, request.updatedPrivateKey());
    assertEquals(memo, request.memo());
    assertEquals(account.accountId(), updatedAccount.accountId());
    assertEquals(updatedPrivateKey, updatedAccount.privateKey());
  }

  @Test
  void testUpdateAccountHooksSuccessful() throws HieroException {
    final Account account =
        Account.of(AccountId.fromString("0.0.12345"), PrivateKey.generateECDSA());
    final HookDetails hookToCreate =
        new HookDetails(
            HookExtensionPoint.ACCOUNT_ALLOWANCE_HOOK,
            101L,
            new EvmHook(ContractId.fromString("0.0.23456")),
            null);
    final List<Long> hooksToDelete = List.of(88L);

    final ArgumentCaptor<AccountHookUpdateRequest> requestCaptor =
        ArgumentCaptor.forClass(AccountHookUpdateRequest.class);
    when(mockProtocolLayerClient.executeAccountHookUpdateTransaction(
            any(AccountHookUpdateRequest.class)))
        .thenReturn(
            new AccountHookUpdateResult(
                TransactionId.generate(account.accountId()), Status.SUCCESS));

    accountClientImpl.updateAccountHooks(account, List.of(hookToCreate), hooksToDelete);

    verify(mockProtocolLayerClient, times(1))
        .executeAccountHookUpdateTransaction(requestCaptor.capture());
    final AccountHookUpdateRequest request = requestCaptor.getValue();
    assertEquals(account, request.toUpdate());
    assertEquals(1, request.hooksToCreate().size());
    assertEquals(hookToCreate, request.hooksToCreate().get(0));
    assertEquals(hooksToDelete, request.hooksToDelete());
  }
}
