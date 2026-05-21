package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a range of timestamps. */
public record TimestampRange(@NonNull Instant from, @Nullable Instant to) {

  public TimestampRange {
    Objects.requireNonNull(from, "from must not be null");
  }
}
