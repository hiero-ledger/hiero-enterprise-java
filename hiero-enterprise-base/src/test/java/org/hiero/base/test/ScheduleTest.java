package org.hiero.base.test;

import java.time.Instant;
import java.util.List;
import org.hiero.base.data.Schedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScheduleTest {

  private static final Instant CONSENSUS_TIMESTAMP = Instant.ofEpochSecond(1793726878L, 208132774L);
  private static final byte[] TRANSACTION_BODY = new byte[] {1, 2, 3};

  @Test
  void testScheduleWithNullIdAndAccounts() {
    // The mirror node documents schedule_id, creator_account_id and payer_account_id as
    // nullable, so the Schedule record must accept null for all three.

    // when
    final Schedule schedule =
        new Schedule(
            null,
            null,
            CONSENSUS_TIMESTAMP,
            null,
            false,
            null,
            null,
            "",
            null,
            List.of(),
            TRANSACTION_BODY,
            false);

    // then
    Assertions.assertNull(schedule.scheduleId());
    Assertions.assertNull(schedule.creatorAccountId());
    Assertions.assertNull(schedule.payerAccountId());
    Assertions.assertEquals(CONSENSUS_TIMESTAMP, schedule.consensusTimestamp());
    Assertions.assertArrayEquals(TRANSACTION_BODY, schedule.transactionBody());
  }

  @Test
  void testScheduleRejectsNullRequiredFields() {
    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            new Schedule(
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                "",
                null,
                List.of(),
                TRANSACTION_BODY,
                false));
    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            new Schedule(
                null,
                null,
                CONSENSUS_TIMESTAMP,
                null,
                false,
                null,
                null,
                null,
                null,
                List.of(),
                TRANSACTION_BODY,
                false));
    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            new Schedule(
                null,
                null,
                CONSENSUS_TIMESTAMP,
                null,
                false,
                null,
                null,
                "",
                null,
                null,
                TRANSACTION_BODY,
                false));
    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            new Schedule(
                null,
                null,
                CONSENSUS_TIMESTAMP,
                null,
                false,
                null,
                null,
                "",
                null,
                List.of(),
                null,
                false));
  }
}
