package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import java.time.Duration;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Detailed information about a Hiero account.
 */
public record AccountInfo(
    @NonNull AccountId accountId,
    @NonNull String contractAccountId,
    boolean isDeleted,
    @NonNull Hbar balance,
    @NonNull Key key,
    boolean isReceiverSignatureRequired,
    @NonNull Instant expirationTime,
    @NonNull Duration autoRenewPeriod,
    @NonNull String memo,
    long ownedNfts,
    int maxAutomaticTokenAssociations,
    @Nullable String alias,
    @NonNull String ledgerId
) {}
