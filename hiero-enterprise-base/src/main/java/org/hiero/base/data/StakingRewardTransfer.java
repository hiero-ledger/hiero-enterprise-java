package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import org.jspecify.annotations.Nullable;

public record StakingRewardTransfer(@Nullable AccountId account, long amount) {}
