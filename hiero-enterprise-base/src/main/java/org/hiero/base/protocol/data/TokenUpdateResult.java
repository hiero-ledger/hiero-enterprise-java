package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenUpdateResult(@NonNull TransactionId transactionId, @NonNull Status status)
    implements TransactionResult {

  public TokenUpdateResult {
    Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
    Objects.requireNonNull(status, "Status cannot be null");
  }
}
