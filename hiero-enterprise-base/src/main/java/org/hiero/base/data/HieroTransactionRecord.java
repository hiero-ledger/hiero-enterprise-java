package org.hiero.base.data;

import com.hedera.hashgraph.sdk.TransactionId;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A record of a transaction on the Hiero network.
 */
public record HieroTransactionRecord(
    @NonNull TransactionId transactionId,
    @NonNull Instant consensusTimestamp,
    @NonNull String transactionHash,
    @NonNull String memo,
    long transactionFee,
    @Nullable String status
) {}
