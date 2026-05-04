package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a contract log returned by the Mirror Node REST API. */
public record ContractLog(
    @NonNull String address,
    @Nullable String bloom,
    @Nullable ContractId contractId,
    @Nullable String data,
    int index,
    @NonNull List<String> topics,
    @Nullable String blockHash,
    @Nullable Long blockNumber,
    @Nullable ContractId rootContractId,
    @Nullable Instant timestamp,
    @Nullable String transactionHash,
    @Nullable Integer transactionIndex) {

  public ContractLog {
    Objects.requireNonNull(address, "address must not be null");
    topics = List.copyOf(Objects.requireNonNull(topics, "topics must not be null"));
  }
}
