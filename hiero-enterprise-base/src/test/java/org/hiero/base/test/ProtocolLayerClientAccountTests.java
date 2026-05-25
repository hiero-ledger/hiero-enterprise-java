package org.hiero.base.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Status;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountBalanceRequest;
import org.hiero.base.protocol.data.AccountBalanceResponse;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.AccountDeleteRequest;
import org.hiero.base.protocol.data.AccountUpdateRequest;
import org.hiero.base.protocol.data.AccountUpdateResult;
import org.hiero.base.test.config.HieroTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProtocolLayerClientAccountTests {

  private static HieroTestContext hieroTestContext;

  private static ProtocolLayerClient protocolLayerClient;

  @BeforeAll
  static void init() {
    hieroTestContext = new HieroTestContext();
    protocolLayerClient = new ProtocolLayerClientImpl(hieroTestContext);
  }

  @Test
  void testAccountBalanceRequest() throws Exception {
    // given
    final Hbar amount = Hbar.from(1L);
    final AccountCreateRequest accountCreateRequest = AccountCreateRequest.of(amount);
    final AccountCreateResult accountCreateResult =
        protocolLayerClient.executeAccountCreateTransaction(accountCreateRequest);
    final AccountId accountId = accountCreateResult.newAccount().accountId();

    // when
    final AccountBalanceRequest accountBalanceRequest = AccountBalanceRequest.of(accountId);
    final AccountBalanceResponse accountBalanceResponse =
        protocolLayerClient.executeAccountBalanceQuery(accountBalanceRequest);

    // then
    Assertions.assertNotNull(accountBalanceResponse);
    Assertions.assertNotNull(accountBalanceResponse.hbars());
    Assertions.assertEquals(amount, accountBalanceResponse.hbars());
  }

  @Test
  void testAccountBalanceRequestForZeroBalance() throws Exception {
    // given
    final AccountCreateRequest accountCreateRequest = AccountCreateRequest.of();
    final AccountCreateResult accountCreateResult =
        protocolLayerClient.executeAccountCreateTransaction(accountCreateRequest);
    final AccountId accountId = accountCreateResult.newAccount().accountId();

    // when
    final AccountBalanceRequest accountBalanceRequest = AccountBalanceRequest.of(accountId);
    final AccountBalanceResponse accountBalanceResponse =
        protocolLayerClient.executeAccountBalanceQuery(accountBalanceRequest);

    // then
    Assertions.assertNotNull(accountBalanceResponse);
    Assertions.assertNotNull(accountBalanceResponse.hbars());
    Assertions.assertEquals(0L, accountBalanceResponse.hbars().toTinybars());
  }

  @Test
  void testAccountBalanceRequestForNotExistingAccount() throws Exception {
    // given
    final AccountCreateRequest accountCreateRequest = AccountCreateRequest.of();
    final AccountCreateResult accountCreateResult =
        protocolLayerClient.executeAccountCreateTransaction(accountCreateRequest);
    final Account account = accountCreateResult.newAccount();
    AccountDeleteRequest accountDeleteRequest = AccountDeleteRequest.of(account);
    protocolLayerClient.executeAccountDeleteTransaction(accountDeleteRequest);

    // when
    final AccountBalanceRequest accountBalanceRequest =
        AccountBalanceRequest.of(account.accountId());

    // then
    Assertions.assertThrows(
        HieroException.class,
        () -> protocolLayerClient.executeAccountBalanceQuery(accountBalanceRequest));
  }

  @Test
  void testAccountUpdateMemoRequest() throws Exception {
    // given
    final AccountCreateResult accountCreateResult =
        protocolLayerClient.executeAccountCreateTransaction(AccountCreateRequest.of());
    final Account account = accountCreateResult.newAccount();
    final AccountUpdateRequest request = AccountUpdateRequest.updateMemo(account, "updated-memo");

    // when
    final AccountUpdateResult result = protocolLayerClient.executeAccountUpdateTransaction(request);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(Status.SUCCESS, result.status());
  }
}
