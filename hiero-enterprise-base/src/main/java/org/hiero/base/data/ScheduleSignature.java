package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents a signature collected for a scheduled transaction. */
public record ScheduleSignature(
    @NonNull Instant consensusTimestamp,
    @NonNull String publicKeyPrefix,
    @NonNull String signature,
    @NonNull String type) {

  public ScheduleSignature {
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(publicKeyPrefix, "publicKeyPrefix must not be null");
    Objects.requireNonNull(signature, "signature must not be null");
    Objects.requireNonNull(type, "type must not be null");
  }
}
