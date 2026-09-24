package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a pending or outstanding token airdrop. */
public record TokenAirdrop(
    long amount,
    @Nullable AccountId receiverId,
    @Nullable AccountId senderId,
    @Nullable Long serialNumber,
    @NonNull TimestampRange timestamp,
    @Nullable TokenId tokenId) {
  public TokenAirdrop {
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
