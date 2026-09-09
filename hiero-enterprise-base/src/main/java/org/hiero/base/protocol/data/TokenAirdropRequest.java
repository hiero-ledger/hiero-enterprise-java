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
 * Request to airdrop one or more NFTs from a sender to one or more receivers via {@code
 * TokenAirdropTransaction}. Unlike a standard transfer, if a receiver lacks available
 * auto-association slots the airdrop may become pending rather than failing.
 *
 * <p>{@code serialToReceiver} maps each NFT serial number to the account that should receive it.
 * Multiple serials may target the same receiver, or each serial may target a different receiver.
 *
 * @see <a href="https://docs.hedera.com/native/tokens/airdrop">Airdrop a token</a>
 */
public record TokenAirdropRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull TokenId tokenId,
    @NonNull Map<Long, AccountId> serialToReceiver,
    @NonNull AccountId sender,
    @NonNull PrivateKey senderKey)
    implements TransactionRequest {

  /** Max NFT ownership changes per {@code TokenAirdropTransaction} on Hedera. */
  static final int MAX_NFT_TRANSFERS = 20;

  public TokenAirdropRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serialToReceiver, "serialToReceiver must not be null");
    Objects.requireNonNull(sender, "sender must not be null");
    Objects.requireNonNull(senderKey, "senderKey must not be null");
    if (serialToReceiver.isEmpty()) {
      throw new IllegalArgumentException("serialToReceiver must not be empty");
    }
    if (serialToReceiver.size() > MAX_NFT_TRANSFERS) {
      throw new IllegalArgumentException(
          "serialToReceiver must not contain more than " + MAX_NFT_TRANSFERS + " entries");
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
  public static TokenAirdropRequest of(
      @NonNull final TokenId tokenId,
      @NonNull final Map<Long, AccountId> serialToReceiver,
      @NonNull final AccountId sender,
      @NonNull final PrivateKey senderKey) {
    return new TokenAirdropRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        tokenId,
        Map.copyOf(serialToReceiver),
        sender,
        senderKey);
  }
}
