package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Request to cancel one or more pending NFT airdrops via {@code TokenCancelAirdropTransaction}. The
 * sender of each pending airdrop must sign the transaction.
 *
 * <p>{@code serialToReceiver} identifies each pending NFT airdrop by token serial and the intended
 * receiver account (same tuple used when the airdrop was created).
 *
 * @see <a href="https://docs.hedera.com/native/tokens/cancel">Cancel a token</a>
 */
public record TokenCancelAirdropRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull Map<Long, AccountId> serialToReceiver,
    @NonNull AccountId sender,
    @NonNull PrivateKey senderKey)
    implements TransactionRequest {

  /** Max pending airdrop cancellations per {@code TokenCancelAirdropTransaction} on Hedera. */
  static final int MAX_PENDING_AIRDROP_CANCELLATIONS = 10;

  public TokenCancelAirdropRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serialToReceiver, "serialToReceiver must not be null");
    Objects.requireNonNull(sender, "sender must not be null");
    Objects.requireNonNull(senderKey, "senderKey must not be null");
    if (serialToReceiver.isEmpty()) {
      throw new IllegalArgumentException("serialToReceiver must not be empty");
    }
    if (serialToReceiver.size() > MAX_PENDING_AIRDROP_CANCELLATIONS) {
      throw new IllegalArgumentException(
          "serialToReceiver must not contain more than "
              + MAX_PENDING_AIRDROP_CANCELLATIONS
              + " entries");
    }
    serialToReceiver.forEach(
        (serial, receiver) -> {
          Objects.requireNonNull(serial, "serial must not be null");
          Objects.requireNonNull(receiver, "receiver must not be null");
          if (serial <= 0) {
            throw new IllegalArgumentException("serial must be positive");
          }
        });
  }

  @NonNull
  public static TokenCancelAirdropRequest of(
      @NonNull final TokenId tokenId,
      final long serial,
      @NonNull final AccountId sender,
      @NonNull final AccountId receiver,
      @NonNull final PrivateKey senderKey) {
    return of(tokenId, List.of(serial), sender, receiver, senderKey);
  }

  @NonNull
  public static TokenCancelAirdropRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serials,
      @NonNull final AccountId sender,
      @NonNull final AccountId receiver,
      @NonNull final PrivateKey senderKey) {
    Objects.requireNonNull(serials, "serials must not be null");
    Objects.requireNonNull(receiver, "receiver must not be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("serials must not be empty");
    }
    final Map<Long, AccountId> serialToReceiver = new LinkedHashMap<>();
    for (final Long serial : serials) {
      serialToReceiver.put(serial, receiver);
    }
    return of(tokenId, serialToReceiver, sender, senderKey);
  }

  @NonNull
  public static TokenCancelAirdropRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final Map<Long, AccountId> serialToReceiver,
      @NonNull final AccountId sender,
      @NonNull final PrivateKey senderKey) {
    return new TokenCancelAirdropRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        Map.copyOf(serialToReceiver),
        sender,
        senderKey);
  }
}
