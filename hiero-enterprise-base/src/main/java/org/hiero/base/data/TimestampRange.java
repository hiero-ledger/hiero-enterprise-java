package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a Mirror Node timestamp validity range. */
public record TimestampRange(@NonNull Instant from, @Nullable Instant to) {
  public TimestampRange {
    Objects.requireNonNull(from, "from must not be null");
  }
}
