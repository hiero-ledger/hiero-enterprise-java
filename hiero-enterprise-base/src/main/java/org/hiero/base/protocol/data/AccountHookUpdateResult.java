package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record AccountHookUpdateResult(@NonNull TransactionId transactionId, @NonNull Status status)
    implements TransactionResult {

  public AccountHookUpdateResult {
    Objects.requireNonNull(transactionId, "transactionId is required");
    Objects.requireNonNull(status, "status is required");
  }
}
