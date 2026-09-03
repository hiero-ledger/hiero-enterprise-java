package org.hiero.microprofile.test;

import static org.hiero.base.data.ContractParam.int256;
import static org.hiero.base.data.ContractParam.string;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.PrivateKey;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.hiero.base.data.ContractCallResult;
import org.hiero.microprofile.ClientProvider;
import org.hiero.test.HieroTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class SmartContractClientTest {
  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private HieroTestUtils hieroTestUtils;

  @Inject private SmartContractClient smartContractClient;
  @Inject private FileClient fileClient;

  @Test
  void testContractCreateByFileId() throws Exception {
    // given
    final Path path =
        Path.of(SmartContractClientTest.class.getResource("/small_contract.bin").getPath());
    final String content = Files.readString(path, StandardCharsets.UTF_8);
    final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    final FileId fileId = fileClient.createFile(bytes);

    // when
    final ContractId contract = smartContractClient.createContract(fileId);

    // then
    Assertions.assertNotNull(contract);
  }

  @Test
  void testContractCreateByBytes() throws Exception {
    // given
    final Path path =
        Path.of(SmartContractClientTest.class.getResource("/small_contract.bin").getPath());
    final String content = Files.readString(path, StandardCharsets.UTF_8);
    final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

    // when
    final ContractId contract = smartContractClient.createContract(bytes);

    // then
    Assertions.assertNotNull(contract);
  }

  @Test
  void testContractCreateSimple() throws Exception {
    // given
    final Path path =
        Path.of(SmartContractClientTest.class.getResource("/small_contract.bin").getPath());

    // when
    final ContractId contract = smartContractClient.createContract(path);

    // then
    Assertions.assertNotNull(contract);
  }

  @Test
  void testContractCreateSimpleWithLargeContract() throws Exception {
    // given
    final Path path =
        Path.of(SmartContractClientTest.class.getResource("/large_contract.bin").getPath());

    // when
    final ContractId contract = smartContractClient.createContract(path);

    // then
    Assertions.assertNotNull(contract);
  }

  @Test
  void testContractCreateSimpleInvalidContent() throws Exception {
    // given
    final byte[] content = "invalid".getBytes(StandardCharsets.UTF_8);

    // then
    Assertions.assertThrows(
        HieroException.class, () -> smartContractClient.createContract(content));
  }

  @Test
  void testContractWithConstructorParam() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/string_param_constructor_contract.bin")
                .getPath());

    // when
    final ContractId contract = smartContractClient.createContract(path, string("Hello"));

    // then
    Assertions.assertNotNull(contract);
  }

  @Test
  void testCallFunction() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);

    // when
    final ContractCallResult result = smartContractClient.callContractFunction(contract, "get");

    // then
    Assertions.assertNotNull(result);
  }

  @Test
  void testCallInvalidFunction() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);

    // when
    Assertions.assertThrows(
        HieroException.class, () -> smartContractClient.callContractFunction(contract, "invalid"));
  }

  @Test
  void testCallFunctionWithParam() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);

    // when
    final ContractCallResult result =
        smartContractClient.callContractFunction(contract, "set", int256(123));

    // then
    Assertions.assertNotNull(result);
  }

  @Test
  void testCallFunctionWithInvalidParam() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);

    // then
    Assertions.assertThrows(
        HieroException.class,
        () -> smartContractClient.callContractFunction(contract, "get", int256(123)));
  }

  @Test
  void testCallFunctionWithResult() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);
    smartContractClient.callContractFunction(contract, "set", int256(123));

    // when
    final ContractCallResult result = smartContractClient.callContractFunction(contract, "get");

    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(BigInteger.valueOf(123), result.getInt256(0));
  }

  @Test
  void testCallFunctionWithWrongResult() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);
    smartContractClient.callContractFunction(contract, "set", int256(123));

    // when
    final ContractCallResult result = smartContractClient.callContractFunction(contract, "get");

    // then
    Assertions.assertThrows(IllegalArgumentException.class, () -> result.getString(0));
  }

  @Test
  void testCallFunctionWithWrongResultCount() throws Exception {
    // given
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contract = smartContractClient.createContract(path);
    smartContractClient.callContractFunction(contract, "set", int256(123));

    // when
    final ContractCallResult result = smartContractClient.callContractFunction(contract, "get");

    // then
    Assertions.assertThrows(IllegalArgumentException.class, () -> result.getString(1));
  }

  @Test
  void testDeleteContract() throws Exception {
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final ContractId contractId = smartContractClient.createContract(path);

    // set initial value
    smartContractClient.callContractFunction(contractId, "set", int256(123));
    final ContractCallResult result1 = smartContractClient.callContractFunction(contractId, "get");

    Assertions.assertNotNull(result1);
    Assertions.assertNotNull(result1.getInt256(0));
    Assertions.assertEquals(123, result1.getUint256(0).intValue());

    // delete contract
    smartContractClient.deleteContract(contractId);
    final ContractCallResult result2 = smartContractClient.callContractFunction(contractId, "get");
    Assertions.assertNotNull(result2);

    // since the contract is mark deleted the function call work without and error, but not return
    // anything
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> result2.getInt256(0));
  }

  @Test
  void testDeleteContractWithAdminKey() throws Exception {
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final PrivateKey adminKey = PrivateKey.generateECDSA();

    final ContractId contractId = smartContractClient.createContract(path, adminKey);

    // set initial value
    smartContractClient.callContractFunction(contractId, "set", int256(123));
    final ContractCallResult result1 = smartContractClient.callContractFunction(contractId, "get");

    Assertions.assertNotNull(result1);
    Assertions.assertNotNull(result1.getInt256(0));
    Assertions.assertEquals(123, result1.getUint256(0).intValue());

    // delete contract
    smartContractClient.deleteContract(contractId, adminKey);
    final ContractCallResult result2 = smartContractClient.callContractFunction(contractId, "get");
    Assertions.assertNotNull(result2);

    // since the contract is mark deleted the function call work without and error, but not return
    // anything
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> result2.getInt256(0));
  }

  @Test
  void testDeleteContractThrowErrorWhenOnInvalidAdminKey() throws Exception {
    final Path path =
        Path.of(
            SmartContractClientTest.class
                .getResource("/uint_getter_setter_contract.bin")
                .getPath());
    final PrivateKey adminKey = PrivateKey.generateECDSA();

    final ContractId contractId = smartContractClient.createContract(path, adminKey);

    // no adminKey default to operator privateKey
    Assertions.assertThrows(
        HieroException.class, () -> smartContractClient.deleteContract(contractId));
    // invalid adminKey
    Assertions.assertThrows(
        HieroException.class,
        () -> smartContractClient.deleteContract(contractId, PrivateKey.generateECDSA()));
  }
}
