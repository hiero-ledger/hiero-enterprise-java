package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record HbarAllowanceApproveRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull Hbar amount,
    @NonNull PrivateKey ownerKey)
    implements TransactionRequest {

  public HbarAllowanceApproveRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(ownerKey, "ownerKey must not be null");
    if (amount.toTinybars() < 0) {
      throw new IllegalArgumentException("amount must be non-negative");
    }
    if (owner.equals(spender)) {
      throw new IllegalArgumentException("owner and spender must be different accounts");
    }
  }

  public static HbarAllowanceApproveRequest of(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final Hbar amount,
      @NonNull final PrivateKey ownerKey) {
    return new HbarAllowanceApproveRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        owner,
        spender,
        amount,
        ownerKey);
  }
}
