package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents a past staking reward payout for an account. */
public record StakingReward(@NonNull AccountId accountId, long amount, @NonNull Instant timestamp) {
  public StakingReward {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
