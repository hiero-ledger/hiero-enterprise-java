package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.BalanceModification;
import org.hiero.base.data.Page;
import org.hiero.base.data.Result;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.protocol.data.TransactionType;
import org.hiero.microprofile.ClientProvider;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class TransactionRepositoryTest {

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private TransactionRepository transactionRepository;

  @Inject private AccountClient accountClient;

  @Inject private HieroTestUtils hieroTestUtils;

  @Test
  void testFindTransactionByAccountId() throws Exception {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page = transactionRepository.findByAccount(account.accountId());
    Assertions.assertNotNull(page);

    final List<TransactionInfo> data = page.getData();
    Assertions.assertFalse(data.isEmpty());
  }

  @Test
  void testFindTransactionByAccountIdGiveEmptyListForAccountIdWithZeroTransaction()
      throws Exception {
    final AccountId accountId = AccountId.fromString("0.0.0");
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page = transactionRepository.findByAccount(accountId);
    Assertions.assertNotNull(page);

    final List<TransactionInfo> data = page.getData();
    Assertions.assertTrue(data.isEmpty());
  }

  @Test
  void testFindTransactionByAccountIdAndType() throws Exception {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page =
        transactionRepository.findByAccountAndType(
            account.accountId(), TransactionType.ACCOUNT_CREATE);
    Assertions.assertNotNull(page);
  }

  @Test
  void testFindTransactionByAccountIdAndResult() throws Exception {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page =
        transactionRepository.findByAccountAndResult(account.accountId(), Result.SUCCESS);
    Assertions.assertNotNull(page);
  }

  @Test
  void testFindTransactionByAccountIdAndBalanceModification() throws Exception {
    final Account account = accountClient.createAccount(1);
    hieroTestUtils.waitForMirrorNodeRecords();
    final Page<TransactionInfo> page =
        transactionRepository.findByAccountAndModification(
            account.accountId(), BalanceModification.DEBIT);
    Assertions.assertNotNull(page);
  }
}
