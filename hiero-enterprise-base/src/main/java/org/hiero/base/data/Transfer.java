package org.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import org.jspecify.annotations.Nullable;

public record Transfer(@Nullable AccountId account, long amount, boolean isApproval) {}
