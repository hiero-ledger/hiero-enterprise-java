package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import org.jspecify.annotations.Nullable;

/** Represents a account info on the Hiero network. */
public record AccountInfo(
    @Nullable AccountId accountId,
    @Nullable String evmAddress,
    long balance,
    long ethereumNonce,
    long pendingReward) {}
