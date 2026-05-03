package org.hiero.microprofile.sample;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
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

@Path("/")
public class HieroEndpoint {

  private final AccountClient client;
  private final BlockRepository blockRepository;
  private final AccountRepository accountRepository;

  @Inject
  public HieroEndpoint(
      final AccountClient client,
      final BlockRepository blockRepository,
      final AccountRepository accountRepository) {
    this.client = client;
    this.blockRepository = blockRepository;
    this.accountRepository = accountRepository;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String createAccount() {
    try {
      final Account account = client.createAccount();
      return "Account created!";
    } catch (final Exception e) {
      throw new RuntimeException("Error in Hedera call", e);
    }
  }

  @GET
  @Path("/blocks")
  @Produces(MediaType.APPLICATION_JSON)
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Error querying blocks", e);
    }
  }

  @GET
  @Path("/api/accounts/{accountId}")
  @Produces(MediaType.APPLICATION_JSON)
  public AccountInfoResponse getAccountInfo(@PathParam("accountId") String accountId)
      throws HieroException {
    final Optional<AccountInfo> info = accountRepository.findById(accountId);
    return new AccountInfoResponse(
        accountId,
        info.map(AccountInfo::balance).map(Object::toString).orElse("Not found"),
        info.isPresent());
  }

  @GET
  @Path("/api/accounts/{accountId}/allowances/crypto")
  @Produces(MediaType.APPLICATION_JSON)
  public List<CryptoAllowance> getCryptoAllowances(@PathParam("accountId") String accountId)
      throws HieroException {
    final Page<CryptoAllowance> page = accountRepository.findCryptoAllowances(accountId);
    return page.getData();
  }

  @GET
  @Path("/api/accounts/{accountId}/allowances/tokens")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TokenAllowance> getTokenAllowances(@PathParam("accountId") String accountId)
      throws HieroException {
    final Page<TokenAllowance> page = accountRepository.findTokenAllowances(accountId);
    return page.getData();
  }

  @GET
  @Path("/api/accounts/{accountId}/allowances/nfts")
  @Produces(MediaType.APPLICATION_JSON)
  public List<NftAllowance> getNftAllowances(@PathParam("accountId") String accountId)
      throws HieroException {
    final Page<NftAllowance> page = accountRepository.findNftAllowances(accountId);
    return page.getData();
  }

  @GET
  @Path("/api/accounts/{accountId}/rewards")
  @Produces(MediaType.APPLICATION_JSON)
  public List<StakingReward> getStakingRewards(@PathParam("accountId") String accountId)
      throws HieroException {
    final Page<StakingReward> page = accountRepository.findStakingRewards(accountId);
    return page.getData();
  }

  @GET
  @Path("/api/accounts/{accountId}/airdrops/outstanding")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TokenAirdrop> getOutstandingAirdrops(@PathParam("accountId") String accountId)
      throws HieroException {
    final Page<TokenAirdrop> page = accountRepository.findOutstandingAirdrops(accountId);
    return page.getData();
  }

  @GET
  @Path("/api/accounts/{accountId}/airdrops/pending")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TokenAirdrop> getPendingAirdrops(@PathParam("accountId") String accountId)
      throws HieroException {
    final Page<TokenAirdrop> page = accountRepository.findPendingAirdrops(accountId);
    return page.getData();
  }

  public record AccountInfoResponse(String accountId, String balance, boolean found) {}
}
