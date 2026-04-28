package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents a range of timestamps. */
public record TimestampRange(@NonNull Instant from, @NonNull Instant to) {

  public TimestampRange {
    Objects.requireNonNull(from, "from must not be null");
    Objects.requireNonNull(to, "to must not be null");
  }
}
