package org.hiero.base.util;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Utility for converting between {@link Instant} and the Mirror Node consensus timestamp string
 * representation in {@code seconds.nanoseconds} format (e.g. {@code "1234567890.000000001"}).
 */
public final class TimestampUtils {

  private static final int NANO_DIGITS = 9;
  private static final String NANO_FORMAT = "%09d";

  private TimestampUtils() {
    throw new UnsupportedOperationException("Utility class must not be instantiated");
  }

  /**
   * Formats the given instant as a Mirror Node consensus timestamp string in {@code
   * seconds.nanoseconds} format with the fractional part padded to nine digits.
   *
   * @param instant the instant to format
   * @return the consensus timestamp string
   */
  @NonNull
  public static String format(@NonNull final Instant instant) {
    Objects.requireNonNull(instant, "instant must not be null");
    final String nanos = String.format(NANO_FORMAT, instant.getNano());
    return instant.getEpochSecond() + "." + nanos;
  }

  /**
   * Parses a Mirror Node consensus timestamp string in {@code seconds} or {@code
   * seconds.nanoseconds} format into an {@link Instant}.
   *
   * @param timestamp the consensus timestamp string
   * @return the parsed instant
   * @throws IllegalArgumentException if the value is not a valid consensus timestamp
   */
  @NonNull
  public static Instant parse(@NonNull final String timestamp) {
    Objects.requireNonNull(timestamp, "timestamp must not be null");
    final String value = timestamp.strip();
    if (value.isEmpty()) {
      throw new IllegalArgumentException("consensus timestamp must not be empty");
    }

    final String[] parts = value.split("\\.", -1);
    if (parts.length > 2) {
      throw new IllegalArgumentException("invalid consensus timestamp format: " + timestamp);
    }

    try {
      final long seconds = Long.parseLong(parts[0]);
      if (seconds < 0) {
        throw new IllegalArgumentException(
            "consensus timestamp seconds must not be negative: " + timestamp);
      }

      int nanos = 0;
      if (parts.length == 2) {
        final String fraction = parts[1];
        if (fraction.isEmpty()
            || fraction.length() > NANO_DIGITS
            || !fraction.chars().allMatch(Character::isDigit)) {
          throw new IllegalArgumentException(
              "invalid consensus timestamp nanoseconds: " + timestamp);
        }
        final String paddedNanos = (fraction + "000000000").substring(0, NANO_DIGITS);
        nanos = Integer.parseInt(paddedNanos);
      }

      return Instant.ofEpochSecond(seconds, nanos);
    } catch (final NumberFormatException e) {
      throw new IllegalArgumentException("invalid consensus timestamp format: " + timestamp, e);
    }
  }
}
