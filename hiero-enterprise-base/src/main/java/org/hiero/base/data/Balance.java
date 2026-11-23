package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import org.jspecify.annotations.Nullable;

/* Represent Balance Response for List token balances Hiero mirror-node */
public record Balance(@Nullable AccountId accountId, long balance, long decimals) {}
