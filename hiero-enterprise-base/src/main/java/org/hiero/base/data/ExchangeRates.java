package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record ExchangeRates(
    @NonNull ExchangeRate currentRate, @NonNull ExchangeRate nextRate, @NonNull Instant timestamp) {
  public ExchangeRates {
    Objects.requireNonNull(currentRate, "currentRate must not be null");
    Objects.requireNonNull(nextRate, "nextRate must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
