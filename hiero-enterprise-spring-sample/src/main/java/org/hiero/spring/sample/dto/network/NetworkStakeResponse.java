package org.hiero.spring.sample.dto.network;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hiero.base.data.NetworkStake;

/** Response DTO for Network Stake Information. */
@Schema(
    name = "Network: Stake",
    description = "Response DTO containing network staking status and parameters.")
public record NetworkStakeResponse(
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
    long unreservedStakingRewardBalance) {
  public static NetworkStakeResponse fromDomain(NetworkStake stake) {
    return new NetworkStakeResponse(
        stake.maxStakeReward(),
        stake.maxStakeRewardPerHbar(),
        stake.maxTotalReward(),
        stake.nodeRewardFeeFraction(),
        stake.reservedStakingRewards(),
        stake.rewardBalanceThreshold(),
        stake.stakeTotal(),
        stake.stakingPeriodDuration(),
        stake.stakingPeriodsStored(),
        stake.stakingRewardFeeFraction(),
        stake.stakingRewardRate(),
        stake.stakingStartThreshold(),
        stake.unreservedStakingRewardBalance());
  }
}
