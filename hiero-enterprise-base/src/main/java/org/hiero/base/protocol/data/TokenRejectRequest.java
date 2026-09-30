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
 * Request to reject one or more NFTs held by an owner via {@code TokenRejectTransaction}. Rejected
 * NFTs are returned to the treasury of the NFT type without charging custom fees. Rejection does
 * not dissociate the owner from the NFT type, and is not possible if the NFT type is paused or the
 * owner is frozen for it.
 *
 * @see <a href="https://docs.hedera.com/native/tokens/reject-airdrop">Reject a token</a>
 */
public record TokenRejectRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull List<Long> serials,
    @NonNull AccountId owner,
    @NonNull PrivateKey ownerKey)
    implements TransactionRequest {

  /** Max rejections per {@code TokenRejectTransaction} on Hedera. */
  static final int MAX_REJECTIONS = 10;

  public TokenRejectRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serials, "serials must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(ownerKey, "ownerKey must not be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("serials must not be empty");
    }
    if (serials.size() > MAX_REJECTIONS) {
      throw new IllegalArgumentException(
          "serials must not contain more than " + MAX_REJECTIONS + " entries");
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
  public static TokenRejectRequest of(
      @NonNull final TokenId tokenId,
      final long serial,
      @NonNull final AccountId owner,
      @NonNull final PrivateKey ownerKey) {
    return of(tokenId, List.of(serial), owner, ownerKey);
  }

  @NonNull
  public static TokenRejectRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serials,
      @NonNull final AccountId owner,
      @NonNull final PrivateKey ownerKey) {
    return new TokenRejectRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        serials,
        owner,
        ownerKey);
  }
}
