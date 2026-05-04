package org.hiero.base.events;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.Map;
import org.jspecify.annotations.NonNull;

/**
 * Represents a decoded smart contract event emitted on a Hiero network.
 */
public record ContractEvent(
    @NonNull ContractId contractId,
    @NonNull String eventName,
    @NonNull Map<String, Object> parameters,
    @NonNull Instant timestamp,
    long blockNumber,
    @NonNull String transactionHash
) {}
