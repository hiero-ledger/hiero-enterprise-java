package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Request for an allowance approval transaction. */
public record AllowanceApproveRequest(
    @Nullable AccountId hbarSpenderId,
    @Nullable Hbar hbarAmount,
    @Nullable TokenId tokenId,
    @Nullable AccountId tokenSpenderId,
    @Nullable Long tokenAmount,
    @Nullable Hbar maxTransactionFee,
    @Nullable Duration transactionValidDuration
) implements TransactionRequest {

    public static AllowanceApproveRequest hbar(@NonNull AccountId spenderId, @NonNull Hbar amount) {
        return new AllowanceApproveRequest(spenderId, amount, null, null, null, null, null);
    }

    public static AllowanceApproveRequest token(@NonNull TokenId tokenId, @NonNull AccountId spenderId, long amount) {
        return new AllowanceApproveRequest(null, null, tokenId, spenderId, amount, null, null);
    }
}
