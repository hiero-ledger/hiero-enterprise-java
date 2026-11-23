package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.ScheduleCreateTransaction;
import com.hedera.hashgraph.sdk.ScheduleId;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.hedera.hashgraph.sdk.proto.SchedulableTransactionBody;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hiero.base.AccountClient;
import org.hiero.base.HieroContext;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.mirrornode.ScheduleRepository;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class ScheduleRepositoryTest {

  private static final String SCHEDULE_MEMO = "hiero-enterprise schedule repository test";

  @Autowired private ScheduleRepository scheduleRepository;

  @Autowired private AccountClient accountClient;

  @Autowired private HieroContext hieroContext;

  @Autowired private HieroTestUtils hieroTestUtils;

  @Test
  void testNullParam() {
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findById((ScheduleId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findById((String) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findByAccount((AccountId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findByAccount((String) null));
  }

  @Test
  void testFindAll() throws Exception {
    final Account creator = accountClient.createAccount(Hbar.from(5));
    createSchedule(creator);
    accountClient.createAccount();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Page<Schedule> result = scheduleRepository.findAll();

    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.getData().isEmpty());
  }

  @Test
  void testFindById() throws Exception {
    final Account creator = accountClient.createAccount(Hbar.from(5));
    final ScheduleId scheduleId = createSchedule(creator);
    accountClient.createAccount();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Optional<Schedule> result = scheduleRepository.findById(scheduleId);

    Assertions.assertTrue(result.isPresent());
    final Schedule schedule = result.get();
    Assertions.assertEquals(scheduleId, schedule.scheduleId());
    Assertions.assertEquals(creator.accountId(), schedule.creatorAccountId());
    Assertions.assertEquals(creator.accountId(), schedule.payerAccountId());
    Assertions.assertEquals(SCHEDULE_MEMO, schedule.memo());
    Assertions.assertNull(schedule.adminKey());
    Assertions.assertFalse(schedule.deleted());
    Assertions.assertTrue(
        schedule.signatures().stream()
            .anyMatch(signature ->
                Arrays.equals(creator.publicKey().toBytesRaw(), signature.publicKeyPrefix())));
    Assertions.assertTrue(
        SchedulableTransactionBody.parseFrom(schedule.transactionBody()).hasCryptoTransfer());
  }

  @Test
  void testFindByIdReturnsEmptyOptional() throws HieroException {
    final Optional<Schedule> result = scheduleRepository.findById(ScheduleId.fromString("0.0.0"));

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testFindByAccount() throws Exception {
    final Account creator = accountClient.createAccount(Hbar.from(5));
    final ScheduleId scheduleId = createSchedule(creator);
    final Account accountWithoutSchedules = accountClient.createAccount();
    hieroTestUtils.waitForMirrorNodeRecords();

    final Page<Schedule> result = scheduleRepository.findByAccount(creator.accountId());
    final Page<Schedule> emptyResult =
        scheduleRepository.findByAccount(accountWithoutSchedules.accountId());

    Assertions.assertEquals(
        List.of(scheduleId), result.getData().stream().map(Schedule::scheduleId).toList());
    Assertions.assertTrue(emptyResult.getData().isEmpty());
  }

  /**
   * Creates a schedule for a transfer from the given account and signs it with the key of that
   * account. The account pays for the schedule, so it is reported as the creator by the mirror
   * node. Waiting for the mirror node only covers transactions that are submitted by the protocol
   * layer client, so tests must submit another transaction (for example an account creation) after
   * this one before they call {@link HieroTestUtils#waitForMirrorNodeRecords()}.
   */
  private ScheduleId createSchedule(final Account creator) throws Exception {
    final Client client = hieroContext.getClient();
    final TransferTransaction scheduledTransfer =
        new TransferTransaction()
            .addHbarTransfer(creator.accountId(), Hbar.fromTinybars(-1))
            .addHbarTransfer(hieroContext.getOperatorAccount().accountId(), Hbar.fromTinybars(1));
    final ScheduleId scheduleId =
        new ScheduleCreateTransaction()
            .setTransactionId(TransactionId.generate(creator.accountId()))
            .setScheduledTransaction(scheduledTransfer)
            .setScheduleMemo(SCHEDULE_MEMO)
            .freezeWith(client)
            .sign(creator.privateKey())
            .execute(client)
            .getReceipt(client)
            .scheduleId;
    Assertions.assertNotNull(scheduleId);
    return scheduleId;
  }
}
