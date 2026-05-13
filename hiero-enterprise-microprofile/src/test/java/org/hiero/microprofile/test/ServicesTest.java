package org.hiero.microprofile.test;

import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.AccountClient;
import org.hiero.base.FileClient;
import org.hiero.base.HookClient;
import org.hiero.base.SmartContractClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class ServicesTest {

  @Inject private ProtocolLayerClient protocolLayerClient;

  @Inject private AccountClient accountClient;

  @Inject private HookClient hookClient;

  @Inject private FileClient fileClient;

  @Inject private SmartContractClient smartContractClient;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Test
  void testServices() throws Exception {
    Assertions.assertNotNull(protocolLayerClient);
    Assertions.assertNotNull(accountClient);
    Assertions.assertNotNull(hookClient);
    Assertions.assertNotNull(fileClient);
    Assertions.assertNotNull(smartContractClient);
  }
}
