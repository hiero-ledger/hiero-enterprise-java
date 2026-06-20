package org.hiero.base.test;

import java.time.Instant;
import org.hiero.base.util.TimestampUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TimestampUtilsTest {

  @Test
  void formatPadsNanosecondsToNineDigits() {
    final Instant instant = Instant.ofEpochSecond(1234567890L, 1);

    Assertions.assertEquals("1234567890.000000001", TimestampUtils.format(instant));
  }

  @Test
  void formatRejectsNull() {
    Assertions.assertThrows(NullPointerException.class, () -> TimestampUtils.format(null));
  }

  @Test
  void parseSupportsSecondsAndNanoseconds() {
    Assertions.assertEquals(
        Instant.ofEpochSecond(1234567890L, 1), TimestampUtils.parse("1234567890.000000001"));
    Assertions.assertEquals(Instant.ofEpochSecond(1234567890L), TimestampUtils.parse("1234567890"));
  }

  @Test
  void parseIsInverseOfFormat() {
    final Instant instant = Instant.ofEpochSecond(1700000000L, 123456789);

    Assertions.assertEquals(instant, TimestampUtils.parse(TimestampUtils.format(instant)));
  }

  @Test
  void parseRejectsNull() {
    Assertions.assertThrows(NullPointerException.class, () -> TimestampUtils.parse(null));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"", "   ", "not-a-timestamp", "1.2.3", "1.", "1.abc", "1.0000000000", "-1.0"})
  void parseRejectsMalformedInput(final String value) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> TimestampUtils.parse(value));
  }
}
