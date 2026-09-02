package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents an HBAR allowance granted by an account. */
public record CryptoAllowance(
    long amount,
    long amountGranted,
    @Nullable AccountId owner,
    @Nullable AccountId spender,
    @NonNull TimestampRange timestamp) {
  public CryptoAllowance {
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
