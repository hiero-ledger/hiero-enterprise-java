package org.hiero.base.data;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a snapshot of account and token balances.
 *
 * @param timestamp the consensus timestamp associated with the balance
 * @param balances the account balances included in the snapshot
 */
public record BalanceSnapshot(@Nullable Instant timestamp, @NonNull List<AccountBalance> balances) {
  public BalanceSnapshot {
    balances = List.copyOf(Objects.requireNonNull(balances, "balances must not be null"));
  }
}
