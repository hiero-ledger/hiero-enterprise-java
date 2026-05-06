package org.hiero.base.data;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record BalanceSnapshot(@NonNull Instant timestamp, @NonNull List<AccountBalance> balances) {
  public BalanceSnapshot {
    Objects.requireNonNull(timestamp, "timestamp must not be null");
    balances = List.copyOf(Objects.requireNonNull(balances, "balances must not be null"));
  }
}
