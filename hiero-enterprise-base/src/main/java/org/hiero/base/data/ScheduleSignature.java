package org.hiero.base.data;

import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Represents a signature that was collected for a scheduled transaction.
 *
 * @param consensusTimestamp the consensus timestamp of the transaction that added the signature
 * @param publicKeyPrefix the prefix of the public key that created the signature
 * @param signature the signature bytes
 * @param type the type of the signature, or {@link ScheduleSignatureType#UNKNOWN} if the mirror
 *     node reports a type that is not known
 */
public record ScheduleSignature(
    @NonNull Instant consensusTimestamp,
    @NonNull byte[] publicKeyPrefix,
    @NonNull byte[] signature,
    @NonNull ScheduleSignatureType type) {

  public ScheduleSignature {
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(publicKeyPrefix, "publicKeyPrefix must not be null");
    Objects.requireNonNull(signature, "signature must not be null");
    Objects.requireNonNull(type, "type must not be null");
  }
}
