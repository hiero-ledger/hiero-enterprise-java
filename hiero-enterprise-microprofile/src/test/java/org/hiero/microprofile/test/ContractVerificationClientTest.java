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
import org.junit.jupiter.api.condition.DisabledIf;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class ContractVerificationClientTest {
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
    return Path.of(ContractVerificationClientTest.class.getResource(resource).getPath());
  }

  @Test
  @Disabled
  @DisabledIf(
      value = "isNotSupportedChain",
      disabledReason = "Verification is currently not supported for custom chains")
  void testContractVerification() throws Exception {
    final Path binPath = getResource("/HelloWorld.bin");
    final ContractId contractId = smartContractClient.createContract(binPath);

    // given
    final String contractName = "HelloWorld";
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
    final Path metadataPath = getResource("/HelloWorld.metadata.json");
    final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);

    // when
    final ContractVerificationState state =
        verificationClient.verify(contractId, contractName, contractSource, contractMetadata);
    // then
    Assertions.assertNotNull(state);
    Assertions.assertEquals(ContractVerificationState.FULL, state);
  }

  @Test
  @Disabled
  @DisabledIf(
      value = "isNotSupportedChain",
      disabledReason = "Verification is currently not supported for custom chains")
  void testCheckIfContractIsVerified() throws Exception {
    final Path binPath = getResource("/HelloWorld.bin");
    final ContractId contractId = smartContractClient.createContract(binPath);

    final ContractVerificationState state1 = verificationClient.checkVerification(contractId);
    Assertions.assertNotNull(state1);
    Assertions.assertEquals(ContractVerificationState.NONE, state1);

    // given
    final String contractName = "HelloWorld";
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
    final Path metadataPath = getResource("/HelloWorld.metadata.json");
    final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);

    // when
    verificationClient.verify(contractId, contractName, contractSource, contractMetadata);
    final ContractVerificationState state2 = verificationClient.checkVerification(contractId);

    // then
    Assertions.assertNotNull(state2);
    Assertions.assertEquals(ContractVerificationState.FULL, state2);
  }

  @Test
  @Disabled
  @DisabledIf(
      value = "isNotSupportedChain",
      disabledReason = "Verification is currently not supported for custom chains")
  void testCheckContractContentVerification() throws Exception {
    final Path binPath = getResource("/HelloWorld.bin");
    final ContractId contractId = smartContractClient.createContract(binPath);

    // given
    final String contractName = "HelloWorld";
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
    final Path metadataPath = getResource("/HelloWorld.metadata.json");
    final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);

    // when
    final String filename = "HelloWorld.sol";
    verificationClient.verify(contractId, contractName, contractSource, contractMetadata);
    final boolean result =
        verificationClient.checkVerification(contractId, filename, contractSource);

    // then
    Assertions.assertTrue(result);
  }

  @Test
  @Disabled
  @DisabledIf(
      value = "isNotSupportedChain",
      disabledReason = "Verification is currently not supported for custom chains")
  void testCheckContractContentVerificationNotMatch() throws Exception {
    final Path binPath = getResource("/HelloWorld.bin");
    final ContractId contractId = smartContractClient.createContract(binPath);

    // given
    final String contractName = "HelloWorld";
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
    final Path metadataPath = getResource("/HelloWorld.metadata.json");
    final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);

    // when
    final String filename = "Hello.sol";
    verificationClient.verify(contractId, contractName, contractSource, contractMetadata);
    final boolean result =
        verificationClient.checkVerification(contractId, filename, "invalid content");

    // then
    Assertions.assertFalse(result);
  }

  @Test
  @Disabled
  @DisabledIf(
      value = "isNotSupportedChain",
      disabledReason = "Verification is currently not supported for custom chains")
  void testCheckContractContentVerificationWhenFilenameNotPresent() throws Exception {
    final Path binPath = getResource("/HelloWorld.bin");
    final ContractId contractId = smartContractClient.createContract(binPath);

    // given
    final String contractName = "HelloWorld";
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
    final Path metadataPath = getResource("/HelloWorld.metadata.json");
    final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);

    // when
    final String filename = "Test.sol";
    verificationClient.verify(contractId, contractName, contractSource, contractMetadata);
    final boolean result =
        verificationClient.checkVerification(contractId, filename, contractSource);

    // then
    Assertions.assertFalse(result);
  }
}
