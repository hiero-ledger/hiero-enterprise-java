package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.BalanceModification;
import org.hiero.base.data.Page;
import org.hiero.base.data.Result;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class TransactionRepositoryTest {
  @Autowired private TransactionRepository transactionRepository;

  @Autowired private AccountClient accountClient;

  @Autowired private HieroTestUtils hieroTestUtils;

  @Test
  void testFindTransactionByAccountId() throws HieroException {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page = transactionRepository.findByAccount(account.accountId());
    Assertions.assertNotNull(page);

    final List<TransactionInfo> data = page.getData();
    Assertions.assertFalse(data.isEmpty());
  }

  @Test
  void testFindTransactionByAccountIdGiveEmptyListForAccountIdWithZeroTransaction()
      throws HieroException {
    final AccountId accountId = AccountId.fromString("0.0.0");
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page = transactionRepository.findByAccount(accountId);
    Assertions.assertNotNull(page);

    final List<TransactionInfo> data = page.getData();
    Assertions.assertTrue(data.isEmpty());
  }

  @Test
  void testFindTransactionByAccountIdAndType() throws HieroException {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page =
        transactionRepository.findByAccountAndType(
            account.accountId(), TransactionType.ACCOUNT_CREATE);
    Assertions.assertNotNull(page);
  }

  @Test
  void testFindTransactionByAccountIdAndResult() throws HieroException {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page =
        transactionRepository.findByAccountAndResult(account.accountId(), Result.SUCCESS);
    Assertions.assertNotNull(page);
  }

  @Test
  void testFindTransactionByAccountIdAndBalanceModification() throws HieroException {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page =
        transactionRepository.findByAccountAndModification(
            account.accountId(), BalanceModification.DEBIT);
    Assertions.assertNotNull(page);
  }
}
