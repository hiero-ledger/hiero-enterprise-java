package org.hiero.spring.test;

import static org.hiero.base.data.ContractParam.int256;

import com.hedera.hashgraph.sdk.ContractId;
import java.nio.file.Path;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.hiero.base.data.Contract;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.ContractRepository;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class ContractRepositoryTest {

  @Autowired private ContractRepository contractRepository;

  @Autowired private SmartContractClient smartContractClient;

  @Autowired private HieroTestUtils hieroTestUtils;

  @Test
  void testNullParam() {
    Assertions.assertThrows(
        NullPointerException.class, () -> contractRepository.findById((String) null));
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
  void testFindAll() throws HieroException {
    // when
    final Page<Contract> contracts = contractRepository.findAll();

    // then
    Assertions.assertNotNull(contracts);
    Assertions.assertNotNull(contracts.getData());
  }

  @Test
  void testFindByIdWithNonExistentContract() throws HieroException {
    // given
    final String nonExistentContractId = "0.0.999999";

    // when
    final Optional<Contract> result = contractRepository.findById(nonExistentContractId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  void testFindResultsAndLogsByIdAfterContractCall() throws Exception {
    // given
    final ContractId contractId = createUintGetterSetterContract();
    smartContractClient.callContractFunction(contractId, "set", int256(123));
    hieroTestUtils.waitForMirrorNodeRecords();

    // when
    final Page<ContractResult> results = contractRepository.findResultsById(contractId);
    final Page<ContractLog> logs = contractRepository.findLogsById(contractId);

    // then
    Assertions.assertNotNull(results);
    Assertions.assertFalse(results.getData().isEmpty());
    Assertions.assertTrue(
        results.getData().stream().anyMatch(result -> contractId.equals(result.contractId())));
    Assertions.assertNotNull(logs);
    Assertions.assertNotNull(logs.getData());
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

  private ContractId createUintGetterSetterContract() throws Exception {
    final Path path =
        Path.of(ContractServiceTest.class.getResource("/uint_getter_setter_contract.bin").getPath());
    return smartContractClient.createContract(path);
  }
}
