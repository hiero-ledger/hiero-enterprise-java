package org.hiero.base.data;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

/** Represents a range of timestamps. */
public record TimestampRange(@Nullable Instant from, @Nullable Instant to) {}
