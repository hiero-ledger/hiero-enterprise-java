package org.hiero.base.test;

import org.hiero.base.data.ScheduleSignatureType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ScheduleSignatureTypeTest {

  @ParameterizedTest
  @EnumSource(ScheduleSignatureType.class)
  void testFromReturnsMatchingType(final ScheduleSignatureType type) {
    Assertions.assertEquals(type, ScheduleSignatureType.from(type.name()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "ED448", "ed25519", "ECDSA_SECP256R1"})
  void testFromReturnsUnknownForUnknownValue(final String value) {
    Assertions.assertEquals(ScheduleSignatureType.UNKNOWN, ScheduleSignatureType.from(value));
  }

  @Test
  void testFromReturnsUnknownForNull() {
    Assertions.assertEquals(ScheduleSignatureType.UNKNOWN, ScheduleSignatureType.from(null));
  }
}
