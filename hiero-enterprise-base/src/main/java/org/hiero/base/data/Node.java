package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Key;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a network node. */
public record Node(
    long nodeId,
    @NonNull AccountId nodeAccountId,
    @Nullable String description,
    @Nullable String memo,
    @Nullable Key publicKey,
    @Nullable String nodeCertHash,
    long stake,
    long minStake,
    long maxStake,
    long stakeRewarded,
    long stakeNotRewarded,
    long rewardRateStart,
    boolean declineReward,
    @Nullable String fileId,
    long stakingPeriod,
    @NonNull TimestampRange timestamp,
    @NonNull List<ServiceEndpoint> serviceEndpoints) {

  public Node {
    Objects.requireNonNull(nodeAccountId, "nodeAccountId must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
    Objects.requireNonNull(serviceEndpoints, "serviceEndpoints must not be null");
  }

  /** Represents a node service endpoint. */
  public record ServiceEndpoint(
      @Nullable String ipAddress,
      int port,
      @Nullable String domainName) {}
}