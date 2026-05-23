package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.ContractId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class ContractRepositoryTest {

  @Inject private ContractRepository contractRepository;

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
        NullPointerException.class, () -> contractRepository.findResultsById((ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> contractRepository.findResultsById((String) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> contractRepository.findLogsById((ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> contractRepository.findLogsById((String) null));
  }

  @Test
  void testFindResultsAndLogsByIdReturnPages() throws HieroException {
    // given
    final String contractId = "0.0.999999";

    // when
    final Page<ContractResult> results = contractRepository.findResultsById(contractId);
    final Page<ContractLog> logs = contractRepository.findLogsById(contractId);

    // then
    Assertions.assertNotNull(results);
    Assertions.assertNotNull(results.getData());
    Assertions.assertNotNull(logs);
    Assertions.assertNotNull(logs.getData());
  }
}
