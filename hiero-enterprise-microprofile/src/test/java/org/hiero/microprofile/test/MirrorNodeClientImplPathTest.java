package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import org.hiero.base.data.BalanceModification;
import org.hiero.base.data.Result;
import org.hiero.base.protocol.data.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MirrorNodeClientImplPathTest {

  private static final AccountId TEST_ACCOUNT = AccountId.fromString("0.0.12345");
  private static final String TRANSACTIONS_PATH = "/api/v1/transactions";
  private static final String TOKENS_PATH = "/api/v1/tokens";

  @Test
  void queryByAccountUsesTransactionsPath() {
    final String path = TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT;

    Assertions.assertEquals(TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT, path);
    Assertions.assertTrue(path.startsWith(TRANSACTIONS_PATH));
    Assertions.assertFalse(path.startsWith(TOKENS_PATH));
  }

  @Test
  void queryByAccountAndTypeUsesProtocolString() {
    final TransactionType type = TransactionType.ACCOUNT_CREATE;
    final String path =
        TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT + "&transactiontype=" + type.getType();

    Assertions.assertEquals(
        TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT + "&transactiontype=CRYPTOCREATEACCOUNT",
        path);
    Assertions.assertFalse(path.contains("ACCOUNT_CREATE"));
  }

  @Test
  void queryByAccountAndResultUsesTransactionsPath() {
    final Result result = Result.SUCCESS;
    final String path =
        TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT + "&result=" + result.name();

    Assertions.assertEquals(
        TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT + "&result=SUCCESS", path);
  }

  @Test
  void queryByAccountAndModificationUsesTransactionsPath() {
    final BalanceModification type = BalanceModification.DEBIT;
    final String path = TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT + "&type=" + type.name();

    Assertions.assertEquals(
        TRANSACTIONS_PATH + "?account.id=" + TEST_ACCOUNT + "&type=DEBIT", path);
  }

  @Test
  void transactionTypeGetTypeReturnsProtocolString() {
    Assertions.assertEquals("CRYPTOCREATEACCOUNT", TransactionType.ACCOUNT_CREATE.getType());
    Assertions.assertEquals("ACCOUNT_CREATE", TransactionType.ACCOUNT_CREATE.name());
    Assertions.assertNotEquals(
        TransactionType.ACCOUNT_CREATE.name(), TransactionType.ACCOUNT_CREATE.getType());
    Assertions.assertEquals("CRYPTOTRANSFER", TransactionType.CRYPTO_TRANSFER.getType());
    Assertions.assertEquals("CONTRACTCALL", TransactionType.CONTRACT_CALL.getType());
    Assertions.assertEquals("TOKENCREATION", TransactionType.TOKEN_CREATE.getType());
  }
}
