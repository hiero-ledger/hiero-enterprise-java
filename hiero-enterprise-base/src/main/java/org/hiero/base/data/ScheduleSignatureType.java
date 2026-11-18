package org.hiero.base.data;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Enumerates the signature types that the mirror node reports for schedule signatures. */
public enum ScheduleSignatureType {
  CONTRACT,
  ED25519,
  RSA_3072,
  ECDSA_384,
  ECDSA_SECP256K1,
  UNKNOWN;

  private static final Map<String, ScheduleSignatureType> map =
      Stream.of(values()).collect(Collectors.toMap(ScheduleSignatureType::name, t -> t));

  /**
   * Returns the signature type for a value reported by the mirror node.
   *
   * @param value the signature type as reported by the mirror node
   * @return the matching signature type, or {@link #UNKNOWN} if the value is {@code null} or not a
   *     known signature type
   */
  public static @NonNull ScheduleSignatureType from(@Nullable String value) {
    if (value == null) {
      return UNKNOWN;
    }
    return map.getOrDefault(value, UNKNOWN);
  }
}
