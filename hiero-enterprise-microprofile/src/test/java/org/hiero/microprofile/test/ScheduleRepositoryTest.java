package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ScheduleId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.data.Page;
import org.hiero.base.data.Schedule;
import org.hiero.base.mirrornode.ScheduleRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class ScheduleRepositoryTest {

  @Inject private ScheduleRepository scheduleRepository;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Test
  void testNullParam() {
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findById((String) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> scheduleRepository.findByAccountId((String) null));
  }

  @Test
  void testFindAll() throws Exception {
    final Page<Schedule> result = scheduleRepository.findAll();

    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getData());
  }

  @Test
  void testFindByIdReturnsEmptyOptionalForInvalidId() throws Exception {
    final ScheduleId scheduleId = ScheduleId.fromString("1.2.3");

    final Optional<Schedule> result = scheduleRepository.findById(scheduleId);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void testFindByAccountIdReturnsEmptyListForAccountWithoutSchedules() throws Exception {
    final AccountId accountId = AccountId.fromString("1.2.3");

    final Page<Schedule> result = scheduleRepository.findByAccountId(accountId);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.getData().isEmpty());
  }
}
