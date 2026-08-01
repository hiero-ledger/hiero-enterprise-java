package org.hiero.base.data;

import com.hedera.hashgraph.sdk.TransactionId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record ChunkInfo(@NonNull TransactionId initialTransactionId, int number, int total) {
  public ChunkInfo {
    Objects.requireNonNull(initialTransactionId, "initialTransactionId must not be null");
  }
}
