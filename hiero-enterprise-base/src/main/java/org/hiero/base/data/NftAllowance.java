package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a non-fungible token allowance granted by an account. */
public record NftAllowance(
    boolean approvedForAll,
    @Nullable AccountId owner,
    @Nullable AccountId spender,
    @NonNull TimestampRange timestamp,
    @Nullable TokenId tokenId) {
  public NftAllowance {
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
