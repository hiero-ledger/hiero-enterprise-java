package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents a single log entry emitted by a smart contract on the Hiero network. */
public record ContractLog(
    @NonNull ContractId contractId,
    @NonNull String transactionHash,
    long blockNumber,
    int logIndex,
    @NonNull List<String> topics,
    @NonNull String data,
    @NonNull String timestamp) {
  public ContractLog {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(transactionHash, "transactionHash must not be null");
    Objects.requireNonNull(topics, "topics must not be null");
    Objects.requireNonNull(data, "data must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
