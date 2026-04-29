package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.time.Instant;
import org.jspecify.annotations.NonNull;

/** Result of an allowance approval transaction. */
public record AllowanceApproveResult(
    @NonNull TransactionId transactionId,
    @NonNull Status status,
    @NonNull byte[] transactionHash,
    @NonNull Instant consensusTimestamp,
    long transactionFee
) implements TransactionResult {}
