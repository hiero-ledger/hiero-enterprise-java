package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record HbarTransferRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull AccountId sender,
    @NonNull AccountId receiver,
    @NonNull Hbar amount,
    @NonNull PrivateKey senderKey)
    implements TransactionRequest {

  public HbarTransferRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(sender, "sender must not be null");
    Objects.requireNonNull(receiver, "receiver must not be null");
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(senderKey, "senderKey must not be null");
    if (amount.toTinybars() <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    if (sender.equals(receiver)) {
      throw new IllegalArgumentException("sender and receiver must be different accounts");
    }
  }

  public static HbarTransferRequest of(
      @NonNull final AccountId sender,
      @NonNull final AccountId receiver,
      @NonNull final Hbar amount,
      @NonNull final PrivateKey senderKey) {
    return new HbarTransferRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        sender,
        receiver,
        amount,
        senderKey);
  }
}
