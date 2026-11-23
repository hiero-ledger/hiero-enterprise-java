package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.NftId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record NftAllowanceDeleteRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull AccountId owner,
    @NonNull NftId nftId,
    @NonNull PrivateKey ownerKey)
    implements TransactionRequest {

  public NftAllowanceDeleteRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(nftId, "nftId must not be null");
    Objects.requireNonNull(ownerKey, "ownerKey must not be null");
    if (nftId.serial < 0) {
      throw new IllegalArgumentException("nft serial must be non-negative");
    }
  }

  public static NftAllowanceDeleteRequest of(
      @NonNull final AccountId owner,
      @NonNull final NftId nftId,
      @NonNull final PrivateKey ownerKey) {
    return new NftAllowanceDeleteRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        owner,
        nftId,
        ownerKey);
  }
}
