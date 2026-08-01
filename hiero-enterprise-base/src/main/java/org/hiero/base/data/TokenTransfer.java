package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import org.jspecify.annotations.Nullable;

public record TokenTransfer(
    @Nullable TokenId tokenId, @Nullable AccountId account, long amount, boolean isApproval) {}
