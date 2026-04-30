package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.mirrornode.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class ScheduleRepositoryTest {

  @Autowired private ScheduleRepository scheduleRepository;

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
}
