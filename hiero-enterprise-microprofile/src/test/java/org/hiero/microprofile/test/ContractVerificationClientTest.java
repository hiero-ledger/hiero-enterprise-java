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
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.base.verification.ContractVerificationState;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class ContractVerificationClientTest {
  @Inject private SmartContractClient smartContractClient;

  @Inject private ContractVerificationClient verificationClient;

  @Inject private HieroConfig hieroConfig;

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

  private boolean isNotSupportedChain() {
    return hieroConfig.chainId().isEmpty();
  }

  @Test
  void testContractVerification() throws Exception {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");

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
  void testCheckIfContractIsVerified() throws Exception {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");

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
  void testCheckContractIsVerifiedForContractNotExists() throws HieroException {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");
    final ContractId contractId = ContractId.fromString("0.0.9999999999");
    final ContractVerificationState result = verificationClient.checkVerification(contractId);
    Assertions.assertEquals(ContractVerificationState.NONE, result);
  }

  @Test
  void testCheckContractContentVerification() throws Exception {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");

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
  void testCheckContractContentVerificationNotMatch() throws Exception {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");

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
  void testCheckContractContentVerificationWhenFilenameNotPresent() throws Exception {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");

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

  @Test
  void testCheckContractContentForContractNotExists() throws Exception {
    Assumptions.assumeFalse(
        hieroConfig.chainId().isEmpty(),
        "Verification is currently not supported for custom chains");

    final ContractId contractId = ContractId.fromString("0.0.9999999999");
    final String filename = "Hello.sol";
    final Path solPath = getResource("/HelloWorld.sol");
    final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);

    Assertions.assertThrows(
        IllegalStateException.class,
        () -> verificationClient.checkVerification(contractId, filename, contractSource));
  }
}
