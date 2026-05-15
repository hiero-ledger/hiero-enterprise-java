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
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.CryptoAllowance;
import org.hiero.base.data.NftAllowance;
import org.hiero.base.data.Page;
import org.hiero.base.data.StakingReward;
import org.hiero.base.data.TokenAirdrop;
import org.hiero.base.data.TokenAllowance;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class AccountRepositoryTest {

  @Inject private AccountRepository accountRepository;

  @Inject private AccountClient accountClient;

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
  void findCryptoAllowances() throws Exception {
    // given
    final AccountId accountId = newAccountVisibleOnMirrorNode();

    // when
    final Page<CryptoAllowance> result = accountRepository.findCryptoAllowances(accountId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void findTokenAllowances() throws Exception {
    // given
    final AccountId accountId = newAccountVisibleOnMirrorNode();

    // when
    final Page<TokenAllowance> result = accountRepository.findTokenAllowances(accountId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void findNftAllowances() throws Exception {
    // given
    final AccountId accountId = newAccountVisibleOnMirrorNode();

    // when
    final Page<NftAllowance> result = accountRepository.findNftAllowances(accountId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void findStakingRewards() throws Exception {
    // given
    final AccountId accountId = newAccountVisibleOnMirrorNode();

    // when
    final Page<StakingReward> result = accountRepository.findStakingRewards(accountId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void findOutstandingAirdrops() throws Exception {
    // given
    final AccountId accountId = newAccountVisibleOnMirrorNode();

    // when
    final Page<TokenAirdrop> result = accountRepository.findOutstandingAirdrops(accountId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void findPendingAirdrops() throws Exception {
    // given
    final AccountId accountId = newAccountVisibleOnMirrorNode();

    // when
    final Page<TokenAirdrop> result = accountRepository.findPendingAirdrops(accountId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  private AccountId newAccountVisibleOnMirrorNode() throws Exception {
    final Account account = accountClient.createAccount();
    // TODO: fix sleep
    Thread.sleep(10_000);
    return account.accountId();
  }
}
