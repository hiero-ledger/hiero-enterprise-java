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
 * Request to airdrop one or more NFTs from a sender to a receiver. Unlike a standard transfer, if
 * the receiver lacks available auto-association slots the airdrop may become pending rather than
 * failing.
 */
public record TokenAirdropRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull List<Long> serials,
    @NonNull AccountId sender,
    @NonNull AccountId receiver,
    @NonNull PrivateKey senderKey)
    implements TransactionRequest {

  public TokenAirdropRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serials, "serials must not be null");
    Objects.requireNonNull(sender, "sender must not be null");
    Objects.requireNonNull(receiver, "receiver must not be null");
    Objects.requireNonNull(senderKey, "senderKey must not be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("serials must not be empty");
    }
    serials.forEach(
        serial -> {
          if (serial < 0) {
            throw new IllegalArgumentException("serial must be positive");
          }
        });
  }

  @NonNull
  public static TokenAirdropRequest of(
      @NonNull final TokenId tokenId,
      final long serial,
      @NonNull final AccountId sender,
      @NonNull final AccountId receiver,
      @NonNull final PrivateKey senderKey) {
    return of(tokenId, List.of(serial), sender, receiver, senderKey);
  }

  @NonNull
  public static TokenAirdropRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serials,
      @NonNull final AccountId sender,
      @NonNull final AccountId receiver,
      @NonNull final PrivateKey senderKey) {
    return new TokenAirdropRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        serials,
        sender,
        receiver,
        senderKey);
  }
}
