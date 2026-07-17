package org.hiero.base.test;

import static org.hiero.base.SmartContractClient.DEFAULT_MAX_TRANSACTION_FEE;
import static org.hiero.base.implementation.ProtocolLayerClientImpl.DEFAULT_GAS;
import static org.hiero.base.protocol.data.ContractCreateRequest.MAX_GAS_LIMIT;
import static org.hiero.base.protocol.data.TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.Hbar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractParam;
import org.hiero.base.implementation.SmartContractClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.ContractCreateRequest;
import org.hiero.base.protocol.data.ContractCreateResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

public class SmartContractClientImplTest {
  private ProtocolLayerClient mockProtocolLayerClient;
  private FileClient mockFileClient;

  private final ArgumentCaptor<ContractCreateRequest> contractCreateRequestCaptor =
      ArgumentCaptor.forClass(ContractCreateRequest.class);

  private SmartContractClientImpl smartContractClient;

  @TempDir private Path tempDir;

  @BeforeEach
  public void setUp() {
    mockProtocolLayerClient = mock(ProtocolLayerClient.class);
    mockFileClient = mock(FileClient.class);
    smartContractClient = new SmartContractClientImpl(mockProtocolLayerClient, mockFileClient);
  }

  @Test
  public void shouldCreateContractWithFileId() throws HieroException {
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final FileId fileId = FileId.fromString("0.0.101");

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(fileId);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertTrue(request.constructorParams().isEmpty());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void sgouldCreateContractWithFileIdAndConstructorParameters() throws HieroException {
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final FileId fileId = FileId.fromString("0.0.101");
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(fileId, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void shouldCreateContractWithFileIdUsesCustomMaxFeeAndGasConfig() throws HieroException {
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final FileId fileId = FileId.fromString("0.0.101");
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);
    final Hbar maxTransactionFee = Hbar.from(20);
    final int gas = 1_000_000;

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId =
        smartContractClient.createContract(fileId, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(maxTransactionFee, request.maxTransactionFee());
    Assertions.assertEquals(gas, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithContents() throws HieroException {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(contents);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertTrue(request.constructorParams().isEmpty());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithContentsAndConstructorParameters() throws HieroException {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(contents, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithContentsWithCustomMaxFeeAndGas() throws HieroException {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);
    final Hbar maxTransactionFee = Hbar.from(20);
    final int gas = 1_000_000;

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId =
        smartContractClient.createContract(contents, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(maxTransactionFee, request.maxTransactionFee());
    Assertions.assertEquals(gas, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithPath() throws Exception {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final Path path = tempDir.resolve("contract.bin");
    Files.write(path, contents);

    // then
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);
    when(mockResponse.contractId()).thenReturn(mockContractId);

    final ContractId contractId = smartContractClient.createContract(path);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertTrue(request.constructorParams().isEmpty());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithPathAndConstructorParameters() throws Exception {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final Path path = tempDir.resolve("contract.bin");
    Files.write(path, contents);

    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);

    // then
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);
    when(mockResponse.contractId()).thenReturn(mockContractId);

    final ContractId contractId = smartContractClient.createContract(path, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithPathWithCustomMaxFeeAndGas() throws Exception {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final Path path = tempDir.resolve("contract.bin");
    Files.write(path, contents);

    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);
    final Hbar maxTransactionFee = Hbar.from(20);
    final int gas = 1_000_000;

    // then
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);
    when(mockResponse.contractId()).thenReturn(mockContractId);

    final ContractId contractId =
        smartContractClient.createContract(path, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(maxTransactionFee, request.maxTransactionFee());
    Assertions.assertEquals(gas, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldThrowNullPointerExceptionForNullArguments() {
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.createContract((FileId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.createContract((byte[]) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.createContract((Path) null));

    // maxTransactionFee
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.createContract(FileId.fromString("0.0.100"), null, 1));
    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            smartContractClient.createContract(
                "6080604052348015600e575f80fd5b506157d".getBytes(), null, 1));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.createContract(tempDir.resolve("contract.bin"), null, 1));
  }

  @Test
  void shouldThrowExceptionIfGasIsLessThanZero() {
    final FileId fileId = FileId.fromString("0.0.101");
    final Hbar maxTransactionFee = Hbar.from(10);

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> smartContractClient.createContract(fileId, maxTransactionFee, -1));
  }

  @Test
  void shouldThrowExceptionIfGasGreaterThanMaxGasLimit() {
    final FileId fileId = FileId.fromString("0.0.101");
    final Hbar maxTransactionFee = Hbar.from(10);
    final int gas = MAX_GAS_LIMIT + 1;

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> smartContractClient.createContract(fileId, maxTransactionFee, gas));
  }
}
