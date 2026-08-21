package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractFunctionResult;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.FileId;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.ContractCallResult;
import org.hiero.base.data.ContractParam;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.ContractCallRequest;
import org.hiero.base.protocol.data.ContractCreateRequest;
import org.hiero.base.protocol.data.ContractCreateResult;
import org.hiero.base.protocol.data.ContractDeleteRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartContractClientImpl implements SmartContractClient {

  private static final Logger log = LoggerFactory.getLogger(SmartContractClientImpl.class);

  private final ProtocolLayerClient protocolLayerClient;

  private final FileClient fileClient;

  private final Account operatorAccount;

  public SmartContractClientImpl(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final FileClient fileClient,
      @NonNull final Account operationalAccount) {
    this.protocolLayerClient =
        Objects.requireNonNull(protocolLayerClient, "protocolLayerClient must not be null");
    this.fileClient = Objects.requireNonNull(fileClient, "fileClient must not be null");
    this.operatorAccount =
        Objects.requireNonNull(operationalAccount, "operatorAccount must not be null");
  }

  @NonNull
  @Override
  public ContractId createContract(
      @NonNull final FileId fileId, @Nullable final ContractParam<?>... constructorParams)
      throws HieroException {
    try {
      final ContractCreateRequest request;
      if (constructorParams == null) {
        request = ContractCreateRequest.of(fileId, operatorAccount.privateKey());
      } else {
        request =
            ContractCreateRequest.of(
                fileId, operatorAccount.privateKey(), Arrays.asList(constructorParams));
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
      @NonNull final byte[] contents, @Nullable final ContractParam<?>... constructorParams)
      throws HieroException {
    try {
      final FileId fileId = fileClient.createFile(contents);
      final ContractId contract = createContract(fileId, constructorParams);
      fileClient.deleteFile(fileId);
      return contract;
    } catch (Exception e) {
      throw new HieroException("Failed to create contract out of byte array", e);
    }
  }

  @NonNull
  @Override
  public ContractId createContract(
      @NonNull final Path pathToBin, @Nullable final ContractParam<?>... constructorParams)
      throws HieroException {
    try {
      final byte[] bytes = Files.readAllBytes(pathToBin);
      return createContract(bytes, constructorParams);
    } catch (Exception e) {
      throw new HieroException("Failed to create contract from path " + pathToBin, e);
    }
  }

  @NonNull
  @Override
  public ContractCallResult callContractFunction(
      @NonNull final ContractId contractId,
      @NonNull final String functionName,
      @Nullable ContractParam<?>... params)
      throws HieroException {
    try {
      final ContractCallRequest request = ContractCallRequest.of(contractId, functionName, params);
      final ContractFunctionResult result =
          protocolLayerClient.executeContractCallTransaction(request).contractFunctionResult();
      return new ContractCallResultImpl(result);
    } catch (Exception e) {
      throw new HieroException(
          "Failed to call function '" + functionName + "' on contract with id " + contractId, e);
    }
  }

  @Override
  public void deleteContract(@NonNull ContractId contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    deleteContract(contractId, operatorAccount.accountId());
  }

  @Override
  public void deleteContract(@NonNull ContractId contractId, @NonNull ContractId toContractId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(toContractId, "toContractId must not be null");
    ContractDeleteRequest request =
        ContractDeleteRequest.of(contractId, toContractId, operatorAccount.privateKey());

    protocolLayerClient.executeContractDeleteTransaction(request);
  }

  @Override
  public void deleteContract(@NonNull ContractId contractId, @NonNull AccountId toAccountId)
      throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(toAccountId, "toAccountId must not be null");
    ContractDeleteRequest request =
        ContractDeleteRequest.of(contractId, toAccountId, operatorAccount.privateKey());

    protocolLayerClient.executeContractDeleteTransaction(request);
  }
}
