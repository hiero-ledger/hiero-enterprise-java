package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a past staking reward payout for an account. */
public record StakingReward(
    @Nullable AccountId accountId, long amount, @NonNull Instant timestamp) {
  public StakingReward {
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
