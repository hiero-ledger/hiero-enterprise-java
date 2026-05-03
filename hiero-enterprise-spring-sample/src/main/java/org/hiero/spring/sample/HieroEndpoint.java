package org.hiero.spring.sample;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.Block;
import org.hiero.base.data.CryptoAllowance;
import org.hiero.base.data.NftAllowance;
import org.hiero.base.data.Page;
import org.hiero.base.data.StakingReward;
import org.hiero.base.data.TokenAirdrop;
import org.hiero.base.data.TokenAllowance;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.mirrornode.BlockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final AccountRepository accountRepository;

  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final AccountRepository accountRepository) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.blockRepository =
        Objects.requireNonNull(blockRepository, "blockRepository must not be null");
    this.accountRepository =
        Objects.requireNonNull(accountRepository, "accountRepository must not be null");
  }

  @GetMapping("/")
  public String createAccount() {
    try {
      final Account account = client.createAccount();
      return "Account " + account.accountId() + " created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GetMapping("/blocks")
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GetMapping("/api/accounts/{accountId}")
  public AccountInfoResponse getAccountInfo(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Optional<AccountInfo> info = accountRepository.findById(accountId);
    return new AccountInfoResponse(
        accountId,
        info.map(AccountInfo::balance).map(Object::toString).orElse("Not found"),
        info.isPresent());
  }

  @GetMapping("/api/accounts/{accountId}/allowances/crypto")
  public List<CryptoAllowance> getCryptoAllowances(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Page<CryptoAllowance> page = accountRepository.findCryptoAllowances(accountId);
    return page.getData();
  }

  @GetMapping("/api/accounts/{accountId}/allowances/tokens")
  public List<TokenAllowance> getTokenAllowances(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Page<TokenAllowance> page = accountRepository.findTokenAllowances(accountId);
    return page.getData();
  }

  @GetMapping("/api/accounts/{accountId}/allowances/nfts")
  public List<NftAllowance> getNftAllowances(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Page<NftAllowance> page = accountRepository.findNftAllowances(accountId);
    return page.getData();
  }

  @GetMapping("/api/accounts/{accountId}/rewards")
  public List<StakingReward> getStakingRewards(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Page<StakingReward> page = accountRepository.findStakingRewards(accountId);
    return page.getData();
  }

  @GetMapping("/api/accounts/{accountId}/airdrops/outstanding")
  public List<TokenAirdrop> getOutstandingAirdrops(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Page<TokenAirdrop> page = accountRepository.findOutstandingAirdrops(accountId);
    return page.getData();
  }

  @GetMapping("/api/accounts/{accountId}/airdrops/pending")
  public List<TokenAirdrop> getPendingAirdrops(@PathVariable("accountId") String accountId)
      throws HieroException {
    final Page<TokenAirdrop> page = accountRepository.findPendingAirdrops(accountId);
    return page.getData();
  }

  public record AccountInfoResponse(String accountId, String balance, boolean found) {}
}
