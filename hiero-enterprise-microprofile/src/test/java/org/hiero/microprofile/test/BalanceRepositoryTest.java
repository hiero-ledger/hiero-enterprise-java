package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountBalance;
import org.hiero.base.data.BalanceSnapshot;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BalanceRepository;
import org.hiero.microprofile.ClientProvider;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class BalanceRepositoryTest {
  @Inject private AccountClient accountClient;
  @Inject private BalanceRepository balanceRepository;
  @Inject private HieroTestUtils hieroTestUtils;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Test
  void testFindAll() throws HieroException {
    final Page<AccountBalance> result = balanceRepository.findAll();
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.getData().isEmpty());
  }

  @Test
  void testFindByAccountId() throws HieroException {
    final Account account = accountClient.createAccount();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Optional<AccountBalance> result = balanceRepository.findByAccount(account.accountId());
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void testFindByNonExistingAccountId() throws HieroException {
    final AccountId accountId = AccountId.fromString("0.0.9999999999");
    final Optional<AccountBalance> result = balanceRepository.findByAccount(accountId);
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testFindByAccountIdAsNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> balanceRepository.findByAccount((AccountId) null));
  }

  @Test
  void testGetSnapshot() throws HieroException {
    final Optional<BalanceSnapshot> result = balanceRepository.getSnapshot();
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }
}
