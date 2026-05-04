package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a contract execution result returned by the Mirror Node REST API. */
public record ContractResult(
    @Nullable String accessList,
    @NonNull String address,
    @Nullable Long amount,
    @Nullable Long blockGasUsed,
    @Nullable String blockHash,
    @Nullable Long blockNumber,
    @Nullable String bloom,
    @Nullable String callResult,
    @Nullable String chainId,
    @Nullable ContractId contractId,
    @NonNull List<ContractId> createdContractIds,
    @Nullable String errorMessage,
    @Nullable String failedInitcode,
    @Nullable String from,
    @Nullable String functionParameters,
    @Nullable Long gasConsumed,
    long gasLimit,
    @Nullable String gasPrice,
    @Nullable Long gasUsed,
    @Nullable String hash,
    @Nullable String maxFeePerGas,
    @Nullable String maxPriorityFeePerGas,
    @Nullable Long nonce,
    @Nullable String r,
    @NonNull String result,
    @Nullable String s,
    @NonNull String status,
    @NonNull Instant timestamp,
    @Nullable String to,
    @Nullable Long transactionIndex,
    @Nullable Integer type,
    @Nullable Integer v) {

  public ContractResult {
    Objects.requireNonNull(address, "address must not be null");
    createdContractIds =
        List.copyOf(
            Objects.requireNonNull(createdContractIds, "createdContractIds must not be null"));
    Objects.requireNonNull(result, "result must not be null");
    Objects.requireNonNull(status, "status must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
