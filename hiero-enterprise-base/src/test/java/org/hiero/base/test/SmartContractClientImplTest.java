package org.hiero.base.test;

import static org.hiero.base.implementation.ProtocolLayerClientImpl.DEFAULT_GAS;
import static org.hiero.base.implementation.ProtocolLayerClientImpl.MAX_GAS_LIMIT;
import static org.hiero.base.protocol.data.ContractCreateRequest.DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE;
import static org.hiero.base.protocol.data.TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE;
import static org.hiero.base.protocol.data.TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractFunctionResult;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.data.Account;
import org.hiero.base.data.ContractParam;
import org.hiero.base.implementation.SmartContractClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.ContractCallRequest;
import org.hiero.base.protocol.data.ContractCallResult;
import org.hiero.base.protocol.data.ContractCreateRequest;
import org.hiero.base.protocol.data.ContractCreateResult;
import org.hiero.base.protocol.data.ContractDeleteRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

public class SmartContractClientImplTest {
  private ProtocolLayerClient mockProtocolLayerClient;
  private FileClient mockFileClient;
  private Account mockOperatorAccount;

  private final ArgumentCaptor<ContractCreateRequest> contractCreateRequestCaptor =
      ArgumentCaptor.forClass(ContractCreateRequest.class);
  private final ArgumentCaptor<ContractCallRequest> contractCallRequestCaptor =
      ArgumentCaptor.forClass(ContractCallRequest.class);
  private final ArgumentCaptor<ContractDeleteRequest> contractDeleteRequestCaptor =
      ArgumentCaptor.forClass(ContractDeleteRequest.class);

  private SmartContractClientImpl smartContractClient;

  @TempDir private Path tempDir;

  @BeforeEach
  public void setUp() {
    mockProtocolLayerClient = mock(ProtocolLayerClient.class);
    mockFileClient = mock(FileClient.class);
    mockOperatorAccount = mock(Account.class);
    smartContractClient =
        new SmartContractClientImpl(mockProtocolLayerClient, mockFileClient, mockOperatorAccount);
  }

  @Test
  public void shouldCreateContractWithFileId() throws HieroException {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final FileId fileId = FileId.fromString("0.0.101");

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(fileId);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertTrue(request.constructorParams().isEmpty());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void shouldCreateContractWithFileIdAndConstructorParameters() throws HieroException {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final FileId fileId = FileId.fromString("0.0.101");
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(fileId, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void shouldCreateContractWithFileIdUsesCustomMaxFeeAndGasConfig() throws HieroException {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final FileId fileId = FileId.fromString("0.0.101");
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);
    final Hbar maxTransactionFee = Hbar.from(20);
    final int gas = 1_000_000;

    // then
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId =
        smartContractClient.createContract(fileId, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
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
    PrivateKey mockAdminKey = PrivateKey.generateECDSA();
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(contents);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertTrue(request.constructorParams().isEmpty());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithContentsAndConstructorParameters() throws HieroException {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);

    // then
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(contents, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockResponse, times(1)).contractId();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithContentsWithCustomMaxFeeAndGas() throws HieroException {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
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
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId =
        smartContractClient.createContract(contents, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
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
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    // given
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final Path path = tempDir.resolve("contract.bin");
    Files.write(path, contents);

    // then
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);
    when(mockResponse.contractId()).thenReturn(mockContractId);

    final ContractId contractId = smartContractClient.createContract(path);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertTrue(request.constructorParams().isEmpty());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithPathAndConstructorParameters() throws Exception {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
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
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);
    when(mockResponse.contractId()).thenReturn(mockContractId);

    final ContractId contractId = smartContractClient.createContract(path, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(2, request.constructorParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.constructorParams());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  public void createContractWithPathWithCustomMaxFeeAndGas() throws Exception {
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();
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
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);
    when(mockResponse.contractId()).thenReturn(mockContractId);

    final ContractId contractId =
        smartContractClient.createContract(path, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, times(1)).privateKey();
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
  void shouldThrowNullPointerExceptionForNullArgumentsOnCreateContract() {
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
  void shouldCreateContractWithFileIdAndAdminKey() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final FileId fileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(fileId, adminKey);

    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();
    verify(mockOperatorAccount, never()).privateKey();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldCreateContractWithContentsAndAdminKey() throws HieroException {
    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final PrivateKey adminKey = PrivateKey.generateECDSA();

    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(contents, adminKey);

    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, never()).privateKey();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldCreateContractWithPathAndAdminKey() throws Exception {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final byte[] contents = "6080604052348015600e575f80fd5b506157d".getBytes();
    final Path path = tempDir.resolve("contract.bin");
    Files.write(path, contents);

    final FileId mockFileId = FileId.fromString("0.0.101");
    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    when(mockFileClient.createFile(contents)).thenReturn(mockFileId);
    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId = smartContractClient.createContract(path, adminKey);

    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockOperatorAccount, never()).privateKey();
    verify(mockFileClient, times(1)).createFile(contents);
    verify(mockFileClient, times(1)).deleteFile(mockFileId);
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(mockFileId, request.fileId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldCreateContractWithFileIdAdminKeyAndCustomMaxFeeAndGas() throws HieroException {
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final FileId fileId = FileId.fromString("0.0.101");
    final Hbar maxTransactionFee = Hbar.from(20);
    final int gas = 1_000_000;
    final ContractParam<String> param = ContractParam.string("Hello");

    final ContractId mockContractId = ContractId.fromString("0.0.1");
    final ContractCreateResult mockResponse = mock(ContractCreateResult.class);

    when(mockResponse.contractId()).thenReturn(mockContractId);
    when(mockProtocolLayerClient.executeContractCreateTransaction(any(ContractCreateRequest.class)))
        .thenReturn(mockResponse);

    final ContractId contractId =
        smartContractClient.createContract(fileId, maxTransactionFee, gas, adminKey, param);

    verify(mockProtocolLayerClient, times(1))
        .executeContractCreateTransaction(contractCreateRequestCaptor.capture());
    verify(mockResponse, times(1)).contractId();

    Assertions.assertEquals(mockContractId, contractId);

    final ContractCreateRequest request = contractCreateRequestCaptor.getValue();
    Assertions.assertEquals(fileId, request.fileId());
    Assertions.assertEquals(adminKey, request.adminKey());
    Assertions.assertEquals(maxTransactionFee, request.maxTransactionFee());
    Assertions.assertEquals(gas, request.gas());
    Assertions.assertEquals(List.of(param), request.constructorParams());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldThrowExceptionIfGasIsLessThanZeroOnCreateContract() {
    final FileId fileId = FileId.fromString("0.0.101");
    final Hbar maxTransactionFee = Hbar.from(10);
    final PrivateKey adminKey = PrivateKey.generateECDSA();

    when(mockOperatorAccount.privateKey()).thenReturn(adminKey);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> smartContractClient.createContract(fileId, maxTransactionFee, -1));

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> smartContractClient.createContract(fileId, maxTransactionFee, -1, adminKey));
  }

  @Test
  void shouldThrowExceptionIfGasGreaterThanMaxGasLimitOnCreateContract() {
    final FileId fileId = FileId.fromString("0.0.101");
    final Hbar maxTransactionFee = Hbar.from(10);
    final int gas = MAX_GAS_LIMIT + 1;
    final PrivateKey adminKey = PrivateKey.generateECDSA();

    when(mockOperatorAccount.privateKey()).thenReturn(adminKey);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> smartContractClient.createContract(fileId, maxTransactionFee, gas));

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> smartContractClient.createContract(fileId, maxTransactionFee, gas, adminKey));
  }

  @Test
  void shouldCallContractFunction() throws Exception {
    final ContractFunctionResult mockCallResult = mock(ContractFunctionResult.class);
    final ContractCallResult response = mock(ContractCallResult.class);

    // given
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String functionName = "doSomething()";

    // then
    when(mockProtocolLayerClient.executeContractCallTransaction(any(ContractCallRequest.class)))
        .thenReturn(response);
    when(response.contractFunctionResult()).thenReturn(mockCallResult);

    final org.hiero.base.data.ContractCallResult result =
        smartContractClient.callContractFunction(contractId, functionName);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCallTransaction(contractCallRequestCaptor.capture());
    verify(response, times(1)).contractFunctionResult();

    Assertions.assertNotNull(result);

    final ContractCallRequest request = contractCallRequestCaptor.getValue();
    Assertions.assertEquals(contractId, request.contractId());
    Assertions.assertEquals(functionName, request.functionName());
    Assertions.assertTrue(request.functionParams().isEmpty());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldCallContractFunctionWithParams() throws Exception {
    final ContractFunctionResult mockCallResult = mock(ContractFunctionResult.class);
    final ContractCallResult response = mock(ContractCallResult.class);

    // given
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String functionName = "doSomething()";
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);

    // then
    when(mockProtocolLayerClient.executeContractCallTransaction(any(ContractCallRequest.class)))
        .thenReturn(response);
    when(response.contractFunctionResult()).thenReturn(mockCallResult);

    final org.hiero.base.data.ContractCallResult result =
        smartContractClient.callContractFunction(contractId, functionName, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCallTransaction(contractCallRequestCaptor.capture());
    verify(response, times(1)).contractFunctionResult();

    Assertions.assertNotNull(result);

    final ContractCallRequest request = contractCallRequestCaptor.getValue();
    Assertions.assertEquals(contractId, request.contractId());
    Assertions.assertEquals(functionName, request.functionName());
    Assertions.assertEquals(2, request.functionParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.functionParams());
    Assertions.assertEquals(DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE, request.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_GAS, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldCallContractFunctionUsingCustomMaxFeeAndGas() throws Exception {
    final ContractFunctionResult mockCallResult = mock(ContractFunctionResult.class);
    final ContractCallResult response = mock(ContractCallResult.class);

    // given
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String functionName = "doSomething()";
    final ContractParam<String> param1 = ContractParam.string("Hello");
    final ContractParam<Long> param2 = ContractParam.int32(10);
    final Hbar maxTransactionFee = Hbar.from(20);
    final int gas = 1_000_000;

    // then
    when(mockProtocolLayerClient.executeContractCallTransaction(any(ContractCallRequest.class)))
        .thenReturn(response);
    when(response.contractFunctionResult()).thenReturn(mockCallResult);

    final org.hiero.base.data.ContractCallResult result =
        smartContractClient.callContractFunction(
            contractId, functionName, maxTransactionFee, gas, param1, param2);

    // verify
    verify(mockProtocolLayerClient, times(1))
        .executeContractCallTransaction(contractCallRequestCaptor.capture());
    verify(response, times(1)).contractFunctionResult();

    Assertions.assertNotNull(result);

    final ContractCallRequest request = contractCallRequestCaptor.getValue();
    Assertions.assertEquals(contractId, request.contractId());
    Assertions.assertEquals(functionName, request.functionName());
    Assertions.assertEquals(2, request.functionParams().size());
    Assertions.assertEquals(List.of(param1, param2), request.functionParams());
    Assertions.assertEquals(maxTransactionFee, request.maxTransactionFee());
    Assertions.assertEquals(gas, request.gas());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, request.transactionValidDuration());
  }

  @Test
  void shouldThrowNullPointerExceptionForNullArgumentsOnFunctionCall() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String functionName = "doSomething";
    // contractId
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.callContractFunction((ContractId) null, functionName));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.callContractFunction((String) null, functionName));

    // functionName
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.callContractFunction(contractId, null));

    // maxTransactionFee
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.callContractFunction(contractId, functionName, null, 1));
  }

  @Test
  void shouldThrowExceptionIfGasIsLessThanZeroOnFunctionCall() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String functionName = "doSomething";
    final Hbar maxTransactionFee = Hbar.from(10);

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            smartContractClient.callContractFunction(
                contractId, functionName, maxTransactionFee, -1));
  }

  @Test
  void shouldThrowExceptionIfGasGreaterThanMaxGasLimitOnFunctionCall() {
    final ContractId contractId = ContractId.fromString("0.0.101");
    final String functionName = "doSomething";
    final Hbar maxTransactionFee = Hbar.from(10);
    final int gas = MAX_GAS_LIMIT + 1;

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            smartContractClient.callContractFunction(
                contractId, functionName, maxTransactionFee, gas));
  }

  @Test
  public void shouldDeleteContract() throws HieroException {
    // mocks
    final AccountId mockAccountId = AccountId.fromString("0.0.1");
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();

    // given
    final ContractId contractId = ContractId.fromString("0.0.100");

    // when
    when(mockOperatorAccount.accountId()).thenReturn(mockAccountId);
    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);

    smartContractClient.deleteContract(contractId);

    // then
    verify(mockOperatorAccount, times(1)).accountId();
    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertEquals(mockAccountId, capture.transferFeeToAccountId());
    Assertions.assertNull(capture.transferFeeToContractId());
    Assertions.assertEquals(mockAdminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldDeleteContractWithAdminKey() throws HieroException {
    // mocks
    final AccountId mockAccountId = AccountId.fromString("0.0.1");

    // given
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final ContractId contractId = ContractId.fromString("0.0.100");

    // when
    when(mockOperatorAccount.accountId()).thenReturn(mockAccountId);

    smartContractClient.deleteContract(contractId, adminKey);

    // then
    verify(mockOperatorAccount, times(1)).accountId();
    verify(mockOperatorAccount, never()).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertEquals(mockAccountId, capture.transferFeeToAccountId());
    Assertions.assertNull(capture.transferFeeToContractId());
    Assertions.assertEquals(adminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldThrowExceptionsOnDeleteContractWithNullContractId() {
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract((ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract((String) null));

    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            smartContractClient.deleteContract(ContractId.fromString("0.0.1"), (PrivateKey) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract("0.0.2", null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (PrivateKey) null));
  }

  @Test
  public void shouldDeleteContractWithTransferAccountId() throws HieroException {
    // mocks
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();

    // given
    final AccountId toAccountId = AccountId.fromString("0.0.1");
    final ContractId contractId = ContractId.fromString("0.0.100");

    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);

    smartContractClient.deleteContract(contractId, toAccountId);

    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertEquals(toAccountId, capture.transferFeeToAccountId());
    Assertions.assertNull(capture.transferFeeToContractId());
    Assertions.assertEquals(mockAdminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldDeleteContractWithTransferAccountIdWithAdminKey() throws HieroException {
    // given
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final AccountId toAccountId = AccountId.fromString("0.0.1");
    final ContractId contractId = ContractId.fromString("0.0.100");

    smartContractClient.deleteContract(contractId, toAccountId, adminKey);

    verify(mockOperatorAccount, never()).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertEquals(toAccountId, capture.transferFeeToAccountId());
    Assertions.assertNull(capture.transferFeeToContractId());
    Assertions.assertEquals(adminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldThrowExceptionsOnDeleteContractWithTransferAccountIdNullParams() {
    final AccountId accountId = AccountId.fromString("0.0.2");
    final ContractId contractId = ContractId.fromString("0.0.100");

    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(contractId, (AccountId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract(null, accountId));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (AccountId) null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(contractId, accountId, null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (AccountId) null, null));
  }

  @Test
  public void shouldDeleteContractWithTransferContractId() throws HieroException {
    // mocks
    final PrivateKey mockAdminKey = PrivateKey.generateECDSA();

    // given
    final ContractId toContractId = ContractId.fromString("0.0.101");
    final ContractId contractId = ContractId.fromString("0.0.100");

    when(mockOperatorAccount.privateKey()).thenReturn(mockAdminKey);

    smartContractClient.deleteContract(contractId, toContractId);

    verify(mockOperatorAccount, times(1)).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertNull(capture.transferFeeToAccountId());
    Assertions.assertEquals(toContractId, capture.transferFeeToContractId());
    Assertions.assertEquals(mockAdminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldDeleteContractWithTransferContractIdWithAdminKey() throws HieroException {
    // given
    final PrivateKey adminKey = PrivateKey.generateECDSA();
    final ContractId toContractId = ContractId.fromString("0.0.101");
    final ContractId contractId = ContractId.fromString("0.0.100");

    smartContractClient.deleteContract(contractId, toContractId, adminKey);

    verify(mockOperatorAccount, never()).privateKey();
    verify(mockProtocolLayerClient, times(1))
        .executeContractDeleteTransaction(contractDeleteRequestCaptor.capture());

    final ContractDeleteRequest capture = contractDeleteRequestCaptor.getValue();
    Assertions.assertNotNull(capture);
    Assertions.assertEquals(contractId, capture.contractId());
    Assertions.assertNull(capture.transferFeeToAccountId());
    Assertions.assertEquals(toContractId, capture.transferFeeToContractId());
    Assertions.assertEquals(adminKey, capture.adminKey());
    Assertions.assertEquals(DEFAULT_MAX_TRANSACTION_FEE, capture.maxTransactionFee());
    Assertions.assertEquals(DEFAULT_TRANSACTION_VALID_DURATION, capture.transactionValidDuration());
  }

  @Test
  public void shouldThrowExceptionsOnDeleteContractWithTransferContractIdNullParams() {
    final ContractId contractId = ContractId.fromString("0.0.100");

    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(contractId, (ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> smartContractClient.deleteContract(null, contractId));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (ContractId) null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(contractId, contractId, null));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> smartContractClient.deleteContract(null, (ContractId) null, null));
  }
}
