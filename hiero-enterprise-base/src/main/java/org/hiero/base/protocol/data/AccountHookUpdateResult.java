package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record AccountHookUpdateResult(
    @NonNull TransactionId transactionId,
    @NonNull Status status,
    @NonNull byte[] transactionHash,
    @NonNull Instant consensusTimestamp,
    @NonNull Hbar transactionFee)
    implements TransactionRecord {

  public AccountHookUpdateResult {
    Objects.requireNonNull(transactionId, "transactionId must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(transactionHash, "transactionHash must not be null");
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(transactionFee, "transactionFee must not be null");
    if (transactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("transactionFee must be non-negative");
    }
  }
}
