package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenWipeResult(
    @NonNull TransactionId transactionId, @NonNull Status status, @NonNull Long totalSupply)
    implements TransactionResult {

  public TokenWipeResult {
    Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
    Objects.requireNonNull(status, "Status cannot be null");
    Objects.requireNonNull(totalSupply, "Total supply cannot be null");
  }
}
