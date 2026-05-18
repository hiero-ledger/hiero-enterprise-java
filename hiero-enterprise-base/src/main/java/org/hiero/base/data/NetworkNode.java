package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record NetworkNode(
    long nodeId,
    @NonNull AccountId nodeAccountId,
    @Nullable String description,
    @Nullable String memo,
    @Nullable String publicKey,
    @Nullable String nodeCertHash,
    @Nullable String fileId,
    @Nullable Boolean declineReward,
    long maxStake,
    long minStake,
    long stake,
    long stakeNotRewarded,
    long stakeRewarded,
    long rewardRateStart,
    @Nullable TimestampRange stakingPeriod,
    @NonNull TimestampRange timestamp,
    @NonNull List<ServiceEndpoint> serviceEndpoints,
    @Nullable String adminKey) {
  public NetworkNode {
    Objects.requireNonNull(nodeAccountId, "nodeAccountId must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
    serviceEndpoints =
        List.copyOf(Objects.requireNonNull(serviceEndpoints, "serviceEndpoints must not be null"));
  }
}
