package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Result of an account update transaction. */
public record AccountUpdateResult(
    @NonNull TransactionId transactionId,
    @NonNull Status status,
    @Nullable byte[] transactionHash,
    @Nullable Instant consensusTimestamp,
    long transactionFee
) implements TransactionResult {
  public AccountUpdateResult {
    Objects.requireNonNull(transactionId, "transactionId must not be null");
    Objects.requireNonNull(status, "status must not be null");
  }

  // Compatibility constructor for main branch
  public AccountUpdateResult(@NonNull TransactionId transactionId, @NonNull Status status) {
    this(transactionId, status, null, null, 0L);
  }
}
