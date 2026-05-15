package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Optional;
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
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class AccountRepositoryTest {

  @Autowired private AccountRepository accountRepository;

  @Autowired private HieroTestUtils hieroTestUtils;

  @Autowired private AccountClient accountClient;

  @Test
  void findById() throws Exception {
    // given
    final Account account = accountClient.createAccount();
    final AccountId newOwner = account.accountId();
    hieroTestUtils.waitForMirrorNodeRecords();

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
    hieroTestUtils.waitForMirrorNodeRecords();
    return account.accountId();
  }
}
