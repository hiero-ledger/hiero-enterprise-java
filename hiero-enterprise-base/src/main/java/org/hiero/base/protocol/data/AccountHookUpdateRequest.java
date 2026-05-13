package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.hiero.base.data.HookDetails;
import org.jspecify.annotations.NonNull;

/** Request model for updating account hooks. */
public record AccountHookUpdateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull Account toUpdate,
    @NonNull List<HookDetails> hooksToCreate,
    @NonNull List<Long> hooksToDelete)
    implements TransactionRequest {

  public AccountHookUpdateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(toUpdate, "toUpdate is required");
    Objects.requireNonNull(hooksToCreate, "hooksToCreate is required");
    Objects.requireNonNull(hooksToDelete, "hooksToDelete is required");
    hooksToCreate.forEach(
        hookDetails -> Objects.requireNonNull(hookDetails, "hooksToCreate must not contain null"));
    hooksToDelete.forEach(
        hookId -> {
          Objects.requireNonNull(hookId, "hooksToDelete must not contain null");
          if (hookId < 0) {
            throw new IllegalArgumentException("hooksToDelete must contain only non-negative ids");
          }
        });

    if (maxTransactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("maxTransactionFee must be non-negative");
    }
    if (transactionValidDuration.isNegative() || transactionValidDuration.isZero()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (hooksToCreate.isEmpty() && hooksToDelete.isEmpty()) {
      throw new IllegalArgumentException("at least one hook action must be set");
    }
  }

  @NonNull
  public static AccountHookUpdateRequest of(
      @NonNull final Account toUpdate,
      @NonNull final List<HookDetails> hooksToCreate,
      @NonNull final List<Long> hooksToDelete) {
    return new AccountHookUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE,
        DEFAULT_TRANSACTION_VALID_DURATION,
        Objects.requireNonNull(toUpdate, "toUpdate is required"),
        List.copyOf(Objects.requireNonNull(hooksToCreate, "hooksToCreate is required")),
        List.copyOf(Objects.requireNonNull(hooksToDelete, "hooksToDelete is required")));
  }
}
