package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Result of a token unfreeze transaction.
 *
 * @param transactionId The ID of the transaction.
 * @param status The status of the transaction.
 * @param transactionHash The hash of the transaction.
 * @param consensusTimestamp The consensus timestamp of the transaction.
 * @param transactionFee The actual fee paid for the transaction.
 */
public record TokenUnfreezeResult(
    @NonNull TransactionId transactionId,
    @NonNull Status status,
    @NonNull byte[] transactionHash,
    @Nullable Instant consensusTimestamp,
    @Nullable Hbar transactionFee)
    implements TransactionResult {

  public TokenUnfreezeResult {
    Objects.requireNonNull(transactionId, "transactionId must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(transactionHash, "transactionHash must not be null");
  }
}
