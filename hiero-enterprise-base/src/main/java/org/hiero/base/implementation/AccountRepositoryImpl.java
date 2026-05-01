package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.data.CryptoAllowance;
import org.hiero.base.data.NftAllowance;
import org.hiero.base.data.Page;
import org.hiero.base.data.StakingReward;
import org.hiero.base.data.TokenAirdrop;
import org.hiero.base.data.TokenAllowance;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.jspecify.annotations.NonNull;

public class AccountRepositoryImpl implements AccountRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public AccountRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public Optional<AccountInfo> findById(@NonNull AccountId accountId) throws HieroException {
    return mirrorNodeClient.queryAccount(accountId);
  }

  @Override
  public Page<CryptoAllowance> findCryptoAllowances(@NonNull AccountId accountId)
      throws HieroException {
    return mirrorNodeClient.queryCryptoAllowances(accountId);
  }

  @Override
  public Page<TokenAllowance> findTokenAllowances(@NonNull AccountId accountId)
      throws HieroException {
    return mirrorNodeClient.queryTokenAllowances(accountId);
  }

  @Override
  public Page<NftAllowance> findNftAllowances(@NonNull AccountId accountId) throws HieroException {
    return mirrorNodeClient.queryNftAllowances(accountId);
  }

  @Override
  public Page<StakingReward> findStakingRewards(@NonNull AccountId accountId)
      throws HieroException {
    return mirrorNodeClient.queryStakingRewards(accountId);
  }

  @Override
  public Page<TokenAirdrop> findOutstandingAirdrops(@NonNull AccountId accountId)
      throws HieroException {
    return mirrorNodeClient.queryOutstandingAirdrops(accountId);
  }

  @Override
  public Page<TokenAirdrop> findPendingAirdrops(@NonNull AccountId accountId)
      throws HieroException {
    return mirrorNodeClient.queryPendingAirdrops(accountId);
  }
}
