package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a smart contract log/event emitted during a contract execution. */
public record ContractLog(
    @Nullable String address,
    @Nullable String bloom,
    @NonNull ContractId contractId,
    @Nullable ContractId rootContractId,
    @NonNull Instant consensusTimestamp,
    @NonNull String data,
    int index,
    @NonNull List<String> topics,
    @NonNull String blockHash,
    long blockNumber,
    @NonNull String transactionHash,
    int transactionIndex) {

  public ContractLog {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(consensusTimestamp, "consensusTimestamp must not be null");
    Objects.requireNonNull(data, "data must not be null");
    Objects.requireNonNull(topics, "topics must not be null");
    topics = List.copyOf(topics);
    Objects.requireNonNull(blockHash, "blockHash must not be null");
    Objects.requireNonNull(transactionHash, "transactionHash must not be null");
  }
}
