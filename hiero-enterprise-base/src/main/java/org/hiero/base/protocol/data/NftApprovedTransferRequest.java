package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;

/**
 * Request to transfer one or more NFTs on behalf of their owner via an approved {@code
 * TransferTransaction}. The spender must have been granted an allowance for the NFTs by the owner.
 * The spender pays the transaction fee and must sign the transaction.
 *
 * @see <a href="https://docs.hedera.com/native/tokens/transfer">Transfer tokens</a>
 */
public record NftApprovedTransferRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull List<Long> serials,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull PrivateKey spenderKey,
    @NonNull AccountId receiver)
    implements TransactionRequest {

  public NftApprovedTransferRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serials, "serials must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(spenderKey, "spenderKey must not be null");
    Objects.requireNonNull(receiver, "receiver must not be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("serials must not be empty");
    }
    final Set<Long> uniqueSerials = new HashSet<>();
    serials.forEach(
        serial -> {
          Objects.requireNonNull(serial, "serial must not be null");
          if (serial <= 0) {
            throw new IllegalArgumentException("serial must be positive");
          }
          if (!uniqueSerials.add(serial)) {
            throw new IllegalArgumentException("serials must not contain duplicates");
          }
        });
    serials = List.copyOf(serials);
  }

  @NonNull
  public static NftApprovedTransferRequest of(
      @NonNull final TokenId tokenId,
      final long serial,
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final PrivateKey spenderKey,
      @NonNull final AccountId receiver) {
    return of(tokenId, List.of(serial), owner, spender, spenderKey, receiver);
  }

  @NonNull
  public static NftApprovedTransferRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serials,
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final PrivateKey spenderKey,
      @NonNull final AccountId receiver) {
    return new NftApprovedTransferRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        serials,
        owner,
        spender,
        spenderKey,
        receiver);
  }
}
