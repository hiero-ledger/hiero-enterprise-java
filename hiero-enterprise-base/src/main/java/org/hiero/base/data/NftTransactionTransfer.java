package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import java.time.Instant;
import java.util.Objects;
import org.hiero.base.protocol.data.TransactionType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a Mirror Node transaction history entry for a specific NFT serial. */
public record NftTransactionTransfer(
    @NonNull Instant consensusTimestamp,
    boolean isApproval,
    int nonce,
    @Nullable AccountId receiverAccountId,
    @Nullable AccountId senderAccountId,
    @NonNull String transactionId,
    @NonNull TransactionType type) {

  public NftTransactionTransfer {
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(transactionId, "transactionId must not be null");
    Objects.requireNonNull(type, "type must not be null");
  }
}
