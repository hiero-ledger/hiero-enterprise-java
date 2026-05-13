package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.EvmHook;
import com.hedera.hashgraph.sdk.HookExtensionPoint;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.Status;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.nio.file.Path;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.HookDetails;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class AccountRepositoryTest {

  @Inject private AccountRepository accountRepository;

  @Inject private AccountClient accountClient;

  @Inject private SmartContractClient smartContractClient;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Test
  void findById() throws Exception {
    // given
    final Account account = accountClient.createAccount();
    final AccountId newOwner = account.accountId();
    // TODO: fix sleep
    Thread.sleep(10_000);

    // when
    final Optional<AccountInfo> result = accountRepository.findById(newOwner);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void updateAccountMemo() throws Exception {
    // given
    final Account account = accountClient.createAccount();

    // when / then
    Assertions.assertDoesNotThrow(() -> accountClient.updateAccountMemo(account, ""));
  }

  @Test
  void updateAccountHooks() throws Exception {
    // given
    final Account account = accountClient.createAccount();
    final Path path =
        Path.of(AccountRepositoryTest.class.getResource("/small_contract.bin").getPath());
    final ContractId contractId = smartContractClient.createContract(path);
    final HookDetails hookToCreate =
        new HookDetails(
            HookExtensionPoint.ACCOUNT_ALLOWANCE_HOOK, 1001L, new EvmHook(contractId), null);

    // when / then
    try {
      accountClient.addHook(account, hookToCreate);
      accountClient.deleteHook(account, hookToCreate.hookId());
    } catch (HieroException e) {
      Assumptions.assumeFalse(
          isHooksNotEnabled(e), "Skipping hook e2e test because hooks are not enabled");
      throw e;
    }
  }

  private static boolean isHooksNotEnabled(Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof PrecheckStatusException precheck
          && precheck.status == Status.HOOKS_NOT_ENABLED) {
        return true;
      }
      current = current.getCause();
    }
    return false;
  }
}
