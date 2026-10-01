package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to approve an NFT allowance. If {@code approveForAll} is {@code true} the spender is
 * approved for all serials of the NFT type, now and in the future, and {@code serialNumbers} must
 * be empty. Otherwise {@code serialNumbers} holds the serials the spender is approved for.
 */
public record NftAllowanceApproveRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull TokenId tokenId,
    @NonNull List<Long> serialNumbers,
    boolean approveForAll,
    @NonNull PrivateKey ownerKey)
    implements TransactionRequest {

  public NftAllowanceApproveRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serialNumbers, "serialNumbers must not be null");
    Objects.requireNonNull(ownerKey, "ownerKey must not be null");
    serialNumbers = List.copyOf(serialNumbers);
    if (transactionValidDuration.isZero() || transactionValidDuration.isNegative()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (owner.equals(spender)) {
      throw new IllegalArgumentException("owner and spender must be different accounts");
    }
    if (approveForAll) {
      if (!serialNumbers.isEmpty()) {
        throw new IllegalArgumentException("serialNumbers must be empty if approveForAll is true");
      }
    } else {
      if (serialNumbers.isEmpty()) {
        throw new IllegalArgumentException("serialNumbers must not be empty");
      }
      if (serialNumbers.stream().anyMatch(serialNumber -> serialNumber < 0)) {
        throw new IllegalArgumentException("nft serial must be non-negative");
      }
    }
  }

  public static NftAllowanceApproveRequest of(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serialNumbers,
      @NonNull final PrivateKey ownerKey) {
    return new NftAllowanceApproveRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        owner,
        spender,
        tokenId,
        serialNumbers,
        false,
        ownerKey);
  }

  public static NftAllowanceApproveRequest forAllSerials(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final TokenId tokenId,
      @NonNull final PrivateKey ownerKey) {
    return new NftAllowanceApproveRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        owner,
        spender,
        tokenId,
        List.of(),
        true,
        ownerKey);
  }
}
