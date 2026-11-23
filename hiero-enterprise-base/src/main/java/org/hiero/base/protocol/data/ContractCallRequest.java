package org.hiero.base.protocol.data;

import static org.hiero.base.implementation.ProtocolLayerClientImpl.MAX_GAS_LIMIT;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Hbar;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.hiero.base.data.ContractParam;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ContractCallRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull ContractId contractId,
    @NonNull String functionName,
    int gas,
    @NonNull List<ContractParam<?>> functionParams)
    implements TransactionRequest {

  public ContractCallRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(contractId, "contractId is required");
    Objects.requireNonNull(functionName, "functionName is required");
    Objects.requireNonNull(functionParams, "functionParams is required");

    if (maxTransactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("maxTransactionFee must be non-negative");
    }
    if (!transactionValidDuration.isPositive()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (functionName.isBlank() || functionName.contains(" ")) {
      throw new IllegalArgumentException("functionName must not be blank or contain spaces");
    }

    if (gas < 0 || gas > MAX_GAS_LIMIT) {
      throw new IllegalArgumentException(
          "gas must be between 0 and " + MAX_GAS_LIMIT + " inclusive");
    }
  }

  @NonNull
  public static ContractCallRequest of(
      @NonNull String contractId,
      @NonNull String functionName,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @Nullable ContractParam<?>... functionParams) {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return of(
        ContractId.fromString(contractId), functionName, maxTransactionFee, gas, functionParams);
  }

  @NonNull
  public static ContractCallRequest of(
      @NonNull ContractId contractId,
      @NonNull String functionName,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @Nullable ContractParam<?>... functionParams) {
    if (functionParams == null) {
      return of(contractId, functionName, maxTransactionFee, gas, List.of());
    } else {
      return of(contractId, functionName, maxTransactionFee, gas, List.of(functionParams));
    }
  }

  @NonNull
  public static ContractCallRequest of(
      @NonNull String contractId,
      @NonNull String functionName,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @NonNull List<ContractParam<?>> functionParams) {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return of(
        ContractId.fromString(contractId), functionName, maxTransactionFee, gas, functionParams);
  }

  @NonNull
  public static ContractCallRequest of(
      @NonNull ContractId contractId,
      @NonNull String functionName,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @NonNull List<ContractParam<?>> functionParams) {
    return new ContractCallRequest(
        maxTransactionFee,
        DEFAULT_TRANSACTION_VALID_DURATION,
        contractId,
        functionName,
        gas,
        List.copyOf(functionParams));
  }
}
