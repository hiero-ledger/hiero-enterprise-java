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
 * Request to transfer one or more NFTs on behalf of their owner via an approved {@code
 * TransferTransaction}. The spender must have been granted an allowance for the NFTs by the owner.
 * The spender pays the transaction fee and must sign the transaction.
 *
 * <p>{@code nftTransfers} maps each NFT type to the serials to transfer and the account that should
 * receive each serial. A transaction must not contain more than {@value #MAX_NFT_TRANSFERS} NFT
 * transfers in total.
 *
 * @see <a href="https://docs.hedera.com/native/tokens/transfer">Transfer tokens</a>
 */
public record NftApprovedTransferRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull Map<TokenId, Map<Long, AccountId>> nftTransfers,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull PrivateKey spenderKey)
    implements TransactionRequest {

  /** Max NFT transfers per {@code TransferTransaction} on Hedera. */
  public static final int MAX_NFT_TRANSFERS = 10;

  public NftApprovedTransferRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(nftTransfers, "nftTransfers must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(spenderKey, "spenderKey must not be null");
    if (nftTransfers.isEmpty()) {
      throw new IllegalArgumentException("nftTransfers must not be empty");
    }
    final Map<TokenId, Map<Long, AccountId>> nftTransfersCopy = new LinkedHashMap<>();
    nftTransfers.forEach(
        (tokenId, serialToReceiver) -> {
          Objects.requireNonNull(tokenId, "tokenId must not be null");
          Objects.requireNonNull(serialToReceiver, "serialToReceiver must not be null");
          if (serialToReceiver.isEmpty()) {
            throw new IllegalArgumentException("serialToReceiver must not be empty");
          }
          serialToReceiver.forEach(
              (serial, receiver) -> {
                Objects.requireNonNull(serial, "serial must not be null");
                Objects.requireNonNull(receiver, "receiver must not be null");
                if (serial <= 0) {
                  throw new IllegalArgumentException("serial must be positive");
                }
              });
          nftTransfersCopy.put(tokenId, Map.copyOf(serialToReceiver));
        });
    final int transferCount = nftTransfersCopy.values().stream().mapToInt(Map::size).sum();
    if (transferCount > MAX_NFT_TRANSFERS) {
      throw new IllegalArgumentException(
          "must not contain more than " + MAX_NFT_TRANSFERS + " NFT transfers");
    }
    nftTransfers = Map.copyOf(nftTransfersCopy);
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
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serials, "serials must not be null");
    Objects.requireNonNull(receiver, "receiver must not be null");
    if (serials.isEmpty()) {
      throw new IllegalArgumentException("serials must not be empty");
    }
    final Map<Long, AccountId> serialToReceiver = new LinkedHashMap<>();
    for (final Long serial : serials) {
      Objects.requireNonNull(serial, "serial must not be null");
      if (serialToReceiver.put(serial, receiver) != null) {
        throw new IllegalArgumentException("serials must not contain duplicates");
      }
    }
    return of(Map.of(tokenId, serialToReceiver), owner, spender, spenderKey);
  }

  @NonNull
  public static NftApprovedTransferRequest of(
      @NonNull final Map<TokenId, Map<Long, AccountId>> nftTransfers,
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final PrivateKey spenderKey) {
    return new NftApprovedTransferRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        nftTransfers,
        owner,
        spender,
        spenderKey);
  }
}
