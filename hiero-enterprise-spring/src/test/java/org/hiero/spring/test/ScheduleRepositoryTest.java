package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.ScheduleCreateTransaction;
import com.hedera.hashgraph.sdk.ScheduleId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransferTransaction;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroContext;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.mirrornode.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class ScheduleRepositoryTest {

  private static final String E2E_SCHEDULE_MEMO = "hiero schedule repository e2e";
  private static final Duration MIRROR_NODE_TIMEOUT = Duration.ofSeconds(30);

  @Autowired private ScheduleRepository scheduleRepository;

  @Autowired private AccountClient accountClient;

  @Autowired private HieroContext hieroContext;

  @Test
  void testNullParam() {
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findById((String) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findByAccountId((String) null));
  }

  @Test
  void testFindAll() throws HieroException {
    final Page<Schedule> result = scheduleRepository.findAll();

    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void testFindByIdReturnsEmptyOptionalForInvalidId() throws HieroException {
    final ScheduleId scheduleId = ScheduleId.fromString("1.2.3");

    final Optional<Schedule> result = scheduleRepository.findById(scheduleId);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testFindByAccountIdReturnsEmptyListForAccountWithoutSchedules() throws HieroException {
    final AccountId accountId = AccountId.fromString("1.2.3");

    final Page<Schedule> result = scheduleRepository.findByAccountId(accountId);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.getData().isEmpty());
  }

  @Test
  void testFindCreatedScheduleByIdAndAccountId() throws Exception {
    final Account signerAccount = accountClient.createAccount(Hbar.fromTinybars(1_000_000));
    final AccountId operatorAccountId = hieroContext.getOperatorAccount().accountId();
    final ScheduleId scheduleId =
        createPendingSchedule(signerAccount.accountId(), operatorAccountId);

    final Schedule schedule = waitForSchedule(scheduleId);
    final Page<Schedule> schedules = scheduleRepository.findByAccountId(operatorAccountId);

    Assertions.assertEquals(scheduleId, schedule.scheduleId());
    Assertions.assertEquals(operatorAccountId, schedule.creatorAccountId());
    Assertions.assertEquals(E2E_SCHEDULE_MEMO, schedule.memo());
    Assertions.assertTrue(
        schedules.getData().stream().anyMatch(s -> scheduleId.equals(s.scheduleId())));
  }

  private ScheduleId createPendingSchedule(
      final AccountId signerAccountId, final AccountId operatorAccountId) throws Exception {
    final TransactionReceipt receipt =
        new ScheduleCreateTransaction()
            .setScheduleMemo(E2E_SCHEDULE_MEMO)
            .setAdminKey(hieroContext.getOperatorAccount().publicKey())
            .setScheduledTransaction(
                new TransferTransaction()
                    .addHbarTransfer(signerAccountId, Hbar.fromTinybars(-1))
                    .addHbarTransfer(operatorAccountId, Hbar.fromTinybars(1)))
            .execute(hieroContext.getClient())
            .getReceipt(hieroContext.getClient());
    Assertions.assertNotNull(receipt.scheduleId);
    return receipt.scheduleId;
  }

  private Schedule waitForSchedule(final ScheduleId scheduleId) throws HieroException {
    final Instant deadline = Instant.now().plus(MIRROR_NODE_TIMEOUT);
    Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
    while (schedule.isEmpty() && Instant.now().isBefore(deadline)) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Interrupted while waiting for schedule", e);
      }
      schedule = scheduleRepository.findById(scheduleId);
    }
    return schedule.orElseThrow(() -> new AssertionError("Schedule not found: " + scheduleId));
  }
}
