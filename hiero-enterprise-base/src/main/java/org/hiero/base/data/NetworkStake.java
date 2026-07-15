package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record NetworkStake(
    long maxStakeReward,
    long maxStakeRewardPerHbar,
    long maxTotalReward,
    double nodeRewardFeeFraction,
    long reservedStakingRewards,
    long rewardBalanceThreshold,
    long stakeTotal,
    long stakingPeriodDuration,
    long stakingPeriodsStored,
    double stakingRewardFeeFraction,
    long stakingRewardRate,
    long stakingStartThreshold,
    long unreservedStakingRewardBalance,
    @NonNull Instant stakingPeriodFrom,
    @Nullable Instant stakingPeriodTo) {
  public NetworkStake {
    Objects.requireNonNull(stakingPeriodFrom, "stakingPeriodFrom must not be null");
  }
}
