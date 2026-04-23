package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record AccountUpdateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull Account toUpdate,
    @Nullable PrivateKey updatedPrivateKey,
    @Nullable String memo)
    implements TransactionRequest {

  public AccountUpdateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    if (maxTransactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("maxTransactionFee must be non-negative");
    }
    if (transactionValidDuration.isNegative() || transactionValidDuration.isZero()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (updatedPrivateKey == null && (memo == null || memo.isBlank())) {
      throw new IllegalArgumentException("at least one update field (key or memo) must be set");
    }
  }

  @NonNull
  public static AccountUpdateRequest updateKey(
      @NonNull Account toUpdate, @NonNull PrivateKey updatedPrivateKey) {
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(updatedPrivateKey, "updatedPrivateKey is required");
    return new AccountUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE,
        DEFAULT_TRANSACTION_VALID_DURATION,
        toUpdate,
        updatedPrivateKey,
        null);
  }

  @NonNull
  public static AccountUpdateRequest updateMemo(@NonNull Account toUpdate, @NonNull String memo) {
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(memo, "memo is required");
    return new AccountUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE, DEFAULT_TRANSACTION_VALID_DURATION, toUpdate, null, memo);
  }

  @NonNull
  public static AccountUpdateRequest of(
      @NonNull Account toUpdate, @NonNull PrivateKey updatedPrivateKey, @NonNull String memo) {
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(updatedPrivateKey, "updatedPrivateKey is required");
    Objects.requireNonNull(memo, "memo is required");
    return new AccountUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE,
        DEFAULT_TRANSACTION_VALID_DURATION,
        toUpdate,
        updatedPrivateKey,
        memo);
  }
}
