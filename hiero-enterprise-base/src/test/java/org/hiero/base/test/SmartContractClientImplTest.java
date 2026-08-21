package org.hiero.base.test;

import static org.hiero.base.protocol.data.TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE;
import static org.hiero.base.protocol.data.TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.PrivateKey;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.implementation.SmartContractClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.ContractDeleteRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class SmartContractClientImplTest {
  private ProtocolLayerClient mockProtocolLayerClient;
  private FileClient mockFileClient;
  private Account mockOperatorAccount;

  private final ArgumentCaptor<ContractDeleteRequest> contractDeleteRequestCaptor =
      ArgumentCaptor.forClass(ContractDeleteRequest.class);

  private SmartContractClientImpl smartContractClient;

  @BeforeEach
  public void setUp() {
    mockProtocolLayerClient = mock(ProtocolLayerClient.class);
    mockFileClient = mock(FileClient.class);
    mockOperatorAccount = mock(Account.class);
    smartContractClient =
        new SmartContractClientImpl(mockProtocolLayerClient, mockFileClient, mockOperatorAccount);
  }

  @Test
  public void shouldDeleteContract() throws HieroException {
    // mocks
    final AccountId mockAccountId = AccountId.fromString("0.0.1");
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();

    // given
    final ContractId contractId = ContractId.fromString("0.0.100");

    // when
    when(mockOperatorAccount.accountId()).thenReturn(mockAccountId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);

    smartContractClient.deleteContract(contractId);

    // then
    verify(mockOperatorAccount, times(1)).accountId();
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertEquals(mockAccountId, capture.transferFeeToAccountId());
    Assertions.assertNull(capture.transferFeeToContractId());
    Assertions.assertEquals(mockAdminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldThrowExceptionsOnDeleteContractWithNullContractId() {
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract((ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract((String) null));
  }

  @Test
  public void shouldDeleteContractWithTransferAccountId() throws HieroException {
    // mocks
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();

    // given
    final AccountId toAccountId = AccountId.fromString("0.0.1");
    final ContractId contractId = ContractId.fromString("0.0.100");

    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);

    smartContractClient.deleteContract(contractId, toAccountId);

    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertEquals(toAccountId, capture.transferFeeToAccountId());
    Assertions.assertNull(capture.transferFeeToContractId());
    Assertions.assertEquals(mockAdminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldThrowExceptionsOnDeleteContractWithTransferAccountIdNullParams() {
    final AccountId accountId = AccountId.fromString("0.0.2");
    final ContractId contractId = ContractId.fromString("0.0.100");

    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(contractId, (AccountId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract(null, accountId));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (AccountId) null));
  }

  @Test
  public void shouldDeleteContractWithTransferContractId() throws HieroException {
    // mocks
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();

    // given
    final ContractId toContractId = ContractId.fromString("0.0.101");
    final ContractId contractId = ContractId.fromString("0.0.100");

    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);

    smartContractClient.deleteContract(contractId, toContractId);

    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertNull(capture.transferFeeToAccountId());
    Assertions.assertEquals(toContractId, capture.transferFeeToContractId());
    Assertions.assertEquals(mockAdminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldThrowExceptionsOnDeleteContractWithTransferContractIdNullParams() {
    final ContractId contractId = ContractId.fromString("0.0.100");

    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(contractId, (ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract(null, contractId));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (ContractId) null));
  }
}
