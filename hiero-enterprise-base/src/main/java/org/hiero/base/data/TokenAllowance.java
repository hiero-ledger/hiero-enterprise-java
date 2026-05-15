package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents a fungible token allowance granted by an account. */
public record TokenAllowance(
    long amount,
    long amountGranted,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull TimestampRange timestamp,
    @NonNull TokenId tokenId) {
  public TokenAllowance {
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
  }
}
