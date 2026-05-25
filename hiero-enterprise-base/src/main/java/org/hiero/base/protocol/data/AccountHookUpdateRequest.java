package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.Hbar;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.hiero.base.data.HookDetails;
import org.jspecify.annotations.NonNull;

public record AccountHookUpdateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull Account account,
    @NonNull List<HookDetails> hooksToCreate,
    @NonNull List<Long> hooksToDelete)
    implements TransactionRequest {

  public AccountHookUpdateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(account, "account is required");
    Objects.requireNonNull(hooksToCreate, "hooksToCreate is required");
    Objects.requireNonNull(hooksToDelete, "hooksToDelete is required");
    if (maxTransactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("maxTransactionFee must be non-negative");
    }
    if (transactionValidDuration.isNegative() || transactionValidDuration.isZero()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    hooksToDelete.forEach(
        hookId -> {
          Objects.requireNonNull(hookId, "hooksToDelete must not contain null values");
          if (hookId < 0) {
            throw new IllegalArgumentException("hook IDs in hooksToDelete must be non-negative");
          }
        });
  }

  @NonNull
  public static AccountHookUpdateRequest addHook(
      @NonNull Account account, @NonNull HookDetails hookToCreate) {
    Objects.requireNonNull(hookToCreate, "hookToCreate is required");
    return new AccountHookUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE,
        DEFAULT_TRANSACTION_VALID_DURATION,
        account,
        List.of(hookToCreate),
        List.of());
  }

  @NonNull
  public static AccountHookUpdateRequest deleteHook(@NonNull Account account, long hookIdToDelete) {
    if (hookIdToDelete < 0) {
      throw new IllegalArgumentException("hookIdToDelete must be non-negative");
    }
    return new AccountHookUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE,
        DEFAULT_TRANSACTION_VALID_DURATION,
        account,
        List.of(),
        List.of(hookIdToDelete));
  }

  @NonNull
  public static AccountHookUpdateRequest of(
      @NonNull Account account,
      @NonNull List<HookDetails> hooksToCreate,
      @NonNull List<Long> hooksToDelete) {
    return new AccountHookUpdateRequest(
        DEFAULT_MAX_TRANSACTION_FEE,
        DEFAULT_TRANSACTION_VALID_DURATION,
        account,
        hooksToCreate,
        hooksToDelete);
  }
}
