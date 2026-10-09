package org.hiero.spring.test;

import com.hedera.hashgraph.sdk.ContractId;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hiero.base.SmartContractClient;
import org.hiero.base.config.HieroConfig;
import org.hiero.base.verification.ContractVerificationClient;
import org.hiero.base.verification.ContractVerificationState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
class ContractVerificationClientTest {

  @Autowired private HieroConfig hieroConfig;

  @Autowired private SmartContractClient smartContractClient;

  @Autowired private ContractVerificationClient verificationClient;

  private Path getResource(String resource) {
    return Path.of(ContractVerificationClientTest.class.getResource(resource).getPath());
  }

  private boolean isNotSupportedChain() {
    return hieroConfig.chainId().isEmpty();
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
