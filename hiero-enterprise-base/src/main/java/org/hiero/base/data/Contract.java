package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Key;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a smart contract on the Hiero network. */
public record Contract(
    @Nullable ContractId contractId,
    @Nullable Key adminKey,
    @Nullable AccountId autoRenewAccount,
    @Nullable Long autoRenewPeriod,
    @Nullable Instant createdTimestamp,
    boolean deleted,
    @Nullable Instant expirationTimestamp,
    @Nullable String fileId,
    @NonNull String evmAddress,
    @NonNull String memo,
    @Nullable Integer maxAutomaticTokenAssociations,
    @Nullable Long nonce,
    @Nullable String obtainerId,
    boolean permanentRemoval,
    @Nullable String proxyAccountId,
    @NonNull Instant fromTimestamp,
    @Nullable Instant toTimestamp,
    @Nullable String bytecode,
    @Nullable String runtimeBytecode) {
  public Contract {
    Objects.requireNonNull(evmAddress, "evmAddress must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    Objects.requireNonNull(fromTimestamp, "fromTimestamp must not be null");
  }
}
