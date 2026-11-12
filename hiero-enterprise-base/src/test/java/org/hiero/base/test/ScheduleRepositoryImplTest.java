package org.hiero.base.test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.implementation.ScheduleRepositoryImpl;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.junit.jupiter.api.Test;

class ScheduleRepositoryImplTest {

  private static final AccountId ACCOUNT_ID = AccountId.fromString("0.0.4866188");
  private static final ScheduleId SCHEDULE_ID = ScheduleId.fromString("0.0.11296896");

  private final MirrorNodeClient mirrorNodeClient = mock(MirrorNodeClient.class);
  private final ScheduleRepositoryImpl repository = new ScheduleRepositoryImpl(mirrorNodeClient);

  @Test
  void constructorRejectsNullMirrorNodeClient() {
    assertThrows(NullPointerException.class, () -> new ScheduleRepositoryImpl(null));
  }

  @Test
  void findAllDelegatesToMirrorNodeClient() throws Exception {
    final Page<Schedule> page = mock(Page.class);
    when(mirrorNodeClient.querySchedules()).thenReturn(page);

    final Page<Schedule> result = repository.findAll();

    assertSame(page, result);
    verify(mirrorNodeClient).querySchedules();
  }

  @Test
  void findByAccountDelegatesToMirrorNodeClient() throws Exception {
    final Page<Schedule> page = mock(Page.class);
    when(mirrorNodeClient.querySchedulesByAccount(ACCOUNT_ID)).thenReturn(page);

    final Page<Schedule> result = repository.findByAccount(ACCOUNT_ID);

    assertSame(page, result);
    verify(mirrorNodeClient).querySchedulesByAccount(ACCOUNT_ID);
  }

  @Test
  void findByAccountWithStringParsesAccountId() throws Exception {
    final Page<Schedule> page = mock(Page.class);
    when(mirrorNodeClient.querySchedulesByAccount(ACCOUNT_ID)).thenReturn(page);

    final Page<Schedule> result = repository.findByAccount("0.0.4866188");

    assertSame(page, result);
    verify(mirrorNodeClient).querySchedulesByAccount(ACCOUNT_ID);
  }

  @Test
  void findByIdDelegatesToMirrorNodeClient() throws Exception {
    final Optional<Schedule> schedule =
        Optional.of(
            new Schedule(
                SCHEDULE_ID,
                null,
                Instant.ofEpochSecond(1_793_726_878L, 208_132_774),
                ACCOUNT_ID,
                false,
                null,
                null,
                "",
                ACCOUNT_ID,
                List.of(),
                new byte[0],
                false));
    when(mirrorNodeClient.queryScheduleById(SCHEDULE_ID)).thenReturn(schedule);

    final Optional<Schedule> result = repository.findById(SCHEDULE_ID);

    assertSame(schedule, result);
    verify(mirrorNodeClient).queryScheduleById(SCHEDULE_ID);
  }

  @Test
  void findByIdWithStringParsesScheduleId() throws Exception {
    when(mirrorNodeClient.queryScheduleById(SCHEDULE_ID)).thenReturn(Optional.empty());

    final Optional<Schedule> result = repository.findById("0.0.11296896");

    assertSame(Optional.empty(), result);
    verify(mirrorNodeClient).queryScheduleById(SCHEDULE_ID);
  }

  @Test
  void rejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> repository.findByAccount((AccountId) null));
    assertThrows(NullPointerException.class, () -> repository.findByAccount((String) null));
    assertThrows(NullPointerException.class, () -> repository.findById((ScheduleId) null));
    assertThrows(NullPointerException.class, () -> repository.findById((String) null));
    verifyNoInteractions(mirrorNodeClient);
  }
}
