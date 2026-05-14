package org.hiero.base.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.data.ScheduleSignature;
import org.hiero.base.data.SinglePage;
import org.hiero.base.implementation.ScheduleRepositoryImpl;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScheduleRepositoryImplTest {

  private final MirrorNodeClient mirrorNodeClient = mock(MirrorNodeClient.class);
  private final ScheduleRepository scheduleRepository =
      new ScheduleRepositoryImpl(mirrorNodeClient);

  @Test
  void constructorRejectsNullMirrorNodeClient() {
    Assertions.assertThrows(NullPointerException.class, () -> new ScheduleRepositoryImpl(null));
  }

  @Test
  void findAllDelegatesToMirrorNodeClient() throws HieroException {
    final Page<Schedule> expected = new SinglePage<>(List.of(schedule()));
    when(mirrorNodeClient.querySchedules()).thenReturn(expected);

    final Page<Schedule> result = scheduleRepository.findAll();

    Assertions.assertSame(expected, result);
    verify(mirrorNodeClient).querySchedules();
  }

  @Test
  void findByIdDelegatesToMirrorNodeClient() throws HieroException {
    final ScheduleId scheduleId = ScheduleId.fromString("0.0.7007");
    final Optional<Schedule> expected = Optional.of(schedule());
    when(mirrorNodeClient.queryScheduleById(scheduleId)).thenReturn(expected);

    final Optional<Schedule> result = scheduleRepository.findById(scheduleId);

    Assertions.assertSame(expected, result);
    verify(mirrorNodeClient).queryScheduleById(scheduleId);
  }

  @Test
  void findByIdStringConvertsAndDelegatesToMirrorNodeClient() throws HieroException {
    final ScheduleId scheduleId = ScheduleId.fromString("0.0.7007");
    final Optional<Schedule> expected = Optional.of(schedule());
    when(mirrorNodeClient.queryScheduleById(scheduleId)).thenReturn(expected);

    final Optional<Schedule> result = scheduleRepository.findById("0.0.7007");

    Assertions.assertSame(expected, result);
    verify(mirrorNodeClient).queryScheduleById(scheduleId);
  }

  @Test
  void findByIdStringRejectsNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findById((String) null));
  }

  @Test
  void findByAccountIdDelegatesToMirrorNodeClient() throws HieroException {
    final AccountId accountId = AccountId.fromString("0.0.1001");
    final Page<Schedule> expected = new SinglePage<>(List.of(schedule()));
    when(mirrorNodeClient.querySchedulesByAccount(accountId)).thenReturn(expected);

    final Page<Schedule> result = scheduleRepository.findByAccountId(accountId);

    Assertions.assertSame(expected, result);
    verify(mirrorNodeClient).querySchedulesByAccount(accountId);
  }

  @Test
  void findByAccountIdStringConvertsAndDelegatesToMirrorNodeClient() throws HieroException {
    final AccountId accountId = AccountId.fromString("0.0.1001");
    final Page<Schedule> expected = new SinglePage<>(List.of(schedule()));
    when(mirrorNodeClient.querySchedulesByAccount(accountId)).thenReturn(expected);

    final Page<Schedule> result = scheduleRepository.findByAccountId("0.0.1001");

    Assertions.assertSame(expected, result);
    verify(mirrorNodeClient).querySchedulesByAccount(accountId);
  }

  @Test
  void findByAccountIdStringRejectsNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findByAccountId((String) null));
  }

  private static Schedule schedule() {
    return new Schedule(
        ScheduleId.fromString("0.0.7007"),
        null,
        false,
        Instant.ofEpochSecond(1_586_567_700L),
        AccountId.fromString("0.0.1001"),
        null,
        Instant.ofEpochSecond(1_586_567_800L),
        "scheduled transfer",
        AccountId.fromString("0.0.1002"),
        List.of(
            new ScheduleSignature(
                Instant.ofEpochSecond(1_586_567_701L), "0x01", "0x02", "ED25519")),
        null,
        false);
  }
}
