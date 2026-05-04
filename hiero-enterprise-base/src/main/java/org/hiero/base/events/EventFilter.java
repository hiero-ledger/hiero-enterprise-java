package org.hiero.base.events;

import com.hedera.hashgraph.sdk.ContractId;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Filter criteria for smart contract events.
 */
public record EventFilter(
    @Nullable ContractId contractId,
    @Nullable List<String> eventSignatures,
    @Nullable Instant fromTimestamp,
    @Nullable Instant toTimestamp,
    @Nullable Long fromBlock,
    @Nullable Long toBlock
) {
  public static EventFilter of(@NonNull ContractId contractId) {
    return new EventFilter(contractId, null, null, null, null, null);
  }

  public static EventFilter of(@NonNull ContractId contractId, @NonNull String eventSignature) {
    return new EventFilter(contractId, Collections.singletonList(eventSignature), null, null, null, null);
  }
}
