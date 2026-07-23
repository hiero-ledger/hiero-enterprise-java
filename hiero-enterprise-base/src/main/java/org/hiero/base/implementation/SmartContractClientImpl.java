package org.hiero.base.implementation;

import static org.hiero.base.implementation.ProtocolLayerClientImpl.MAX_GAS_LIMIT;

import com.hedera.hashgraph.sdk.ContractFunctionResult;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.Hbar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.hiero.base.data.ContractCallResult;
import org.hiero.base.data.ContractParam;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.ContractCallRequest;
import org.hiero.base.protocol.data.ContractCreateRequest;
import org.hiero.base.protocol.data.ContractCreateResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartContractClientImpl implements SmartContractClient {

  private static final Logger log = LoggerFactory.getLogger(SmartContractClientImpl.class);

  private final ProtocolLayerClient protocolLayerClient;

  private final FileClient fileClient;

  public SmartContractClientImpl(
      @NonNull final ProtocolLayerClient protocolLayerClient, FileClient fileClient) {
    this.protocolLayerClient =
        Objects.requireNonNull(protocolLayerClient, "protocolLayerClient must not be null");
    this.fileClient = Objects.requireNonNull(fileClient, "fileClient must not be null");
  }

  @NonNull
  @Override
  public ContractId createContract(
      @NonNull final FileId fileId,
      @NonNull final Hbar maxTransactionFee,
      final int gas,
      @Nullable final ContractParam<?>... constructorParams)
      throws HieroException {
    Objects.requireNonNull(fileId, "fileId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");

    if (gas < 0 || gas > MAX_GAS_LIMIT) {
      throw new IllegalArgumentException(
          "gas must be between 0 and " + MAX_GAS_LIMIT + " inclusive");
    }

    try {
      final ContractCreateRequest request;
      if (constructorParams == null) {
        request = ContractCreateRequest.of(fileId, maxTransactionFee, gas);
      } else {
        request =
            ContractCreateRequest.of(
                fileId, maxTransactionFee, gas, Arrays.asList(constructorParams));
      }
      final ContractCreateResult result =
          protocolLayerClient.executeContractCreateTransaction(request);
      return result.contractId();
    } catch (Exception e) {
      throw new HieroException("Failed to create contract with fileId " + fileId, e);
    }
  }

  @NonNull
  @Override
  public ContractId createContract(
      @NonNull final byte[] contents,
      @NonNull final Hbar maxTransactionFee,
      final int gas,
      @Nullable final ContractParam<?>... constructorParams)
      throws HieroException {
    Objects.requireNonNull(contents, "contents must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");

    try {
      final FileId fileId = fileClient.createFile(contents);
      final ContractId contract = createContract(fileId, maxTransactionFee, gas, constructorParams);
      fileClient.deleteFile(fileId);
      return contract;
    } catch (Exception e) {
      throw new HieroException("Failed to create contract out of byte array", e);
    }
  }

  @NonNull
  @Override
  public ContractId createContract(
      @NonNull final Path pathToBin,
      @NonNull final Hbar maxTransactionFee,
      final int gas,
      @Nullable final ContractParam<?>... constructorParams)
      throws HieroException {
    Objects.requireNonNull(pathToBin, "pathToBin must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");

    try {
      final byte[] bytes = Files.readAllBytes(pathToBin);
      return createContract(bytes, maxTransactionFee, gas, constructorParams);
    } catch (Exception e) {
      throw new HieroException("Failed to create contract from path " + pathToBin, e);
    }
  }

  @NonNull
  @Override
  public ContractCallResult callContractFunction(
      @NonNull final ContractId contractId,
      @NonNull final String functionName,
      @NonNull final Hbar maxTransactionFee,
      final int gas,
      @Nullable ContractParam<?>... params)
      throws HieroException {

    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(functionName, "functionName must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");

    if (gas < 0 || gas > MAX_GAS_LIMIT) {
      throw new IllegalArgumentException(
          "gas must be between 0 and " + MAX_GAS_LIMIT + " inclusive");
    }

    try {
      final ContractCallRequest request =
          ContractCallRequest.of(contractId, functionName, maxTransactionFee, gas, params);
      final ContractFunctionResult result =
          protocolLayerClient.executeContractCallTransaction(request).contractFunctionResult();
      return new ContractCallResultImpl(result);
    } catch (Exception e) {
      throw new HieroException(
          "Failed to call function '" + functionName + "' on contract with id " + contractId, e);
    }
  }
}
