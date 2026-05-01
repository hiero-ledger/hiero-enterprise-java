package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a pending or outstanding token airdrop. */
public record TokenAirdrop(
    long amount,
    @NonNull AccountId receiverId,
    @NonNull AccountId senderId,
    @Nullable Long serialNumber,
    @NonNull TimestampRange timestamp,
    @NonNull TokenId tokenId) {
  public TokenAirdrop {
    Objects.requireNonNull(receiverId, "receiverId must not be null");
    Objects.requireNonNull(senderId, "senderId must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
  }
}
