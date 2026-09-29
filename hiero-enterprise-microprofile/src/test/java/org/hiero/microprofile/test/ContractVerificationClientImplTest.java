package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.ContractId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.SmartContractClient;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.base.verification.ContractVerificationState;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class ContractVerificationClientImplTest {
  @Inject private SmartContractClient smartContractClient;

  @Inject private ContractVerificationClient verificationClient;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  private Path getResource(String resource) {
    return Path.of(ContractVerificationClientImplTest.class.getResource(resource).getPath());
  }

  @Test
  @Disabled
  void test() throws Exception {
    // given
    final String contractName = "HelloWorld";
    final Path binPath = getResource("/HelloWorld.bin");
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
    final Path metadataPath = getResource("/HelloWorld.metadata.json");
    final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);
    final ContractId contractId = smartContractClient.createContract(binPath);

    // when
    final ContractVerificationState state =
        verificationClient.verify(contractId, contractName, contractSource, contractMetadata);
    // then
    Assertions.assertEquals(ContractVerificationState.FULL, state);
  }
}
