package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents an HBAR allowance granted by an account. */
public record CryptoAllowance(
    long amount,
    long amountGranted,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull TimestampRange timestamp) {
  public CryptoAllowance {
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
