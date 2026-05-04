package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a raw smart contract log as returned by the Mirror Node.
 */
public record ContractLog(
    @NonNull ContractId contractId,
    @NonNull String address,
    @NonNull String data,
    @NonNull List<String> topics,
    @NonNull Instant consensusTimestamp,
    @NonNull String bloom,
    @NonNull String blockHash,
    long blockNumber,
    @NonNull String transactionHash,
    int transactionIndex,
    int logIndex,
    @Nullable ContractId rootContractId
) {}
