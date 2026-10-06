package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;

/**
 * Request to approve NFT allowances for one or more NFT types in a single {@code
 * AccountAllowanceApproveTransaction}.
 *
 * <p>{@code serialNumbers} maps each NFT type to the serials the spender is approved for. {@code
 * allSerialsTokenIds} holds the NFT types for which the spender is approved for all serials, now
 * and in the future. An NFT type must not appear in both.
 *
 * <p>Every serial and every all-serials NFT type counts as one allowance. A transaction must not
 * contain more than {@value #MAX_ALLOWANCES} allowances.
 */
public record NftAllowanceApproveRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull AccountId owner,
    @NonNull AccountId spender,
    @NonNull Map<TokenId, List<Long>> serialNumbers,
    @NonNull Set<TokenId> allSerialsTokenIds,
    @NonNull PrivateKey ownerKey)
    implements TransactionRequest {

  /** Max allowances per {@code AccountAllowanceApproveTransaction} on Hedera. */
  public static final int MAX_ALLOWANCES = 20;

  /**
   * Approving many allowances in one transaction costs more than {@link
   * TransactionRequest#DEFAULT_MAX_TRANSACTION_FEE}.
   */
  public static final Hbar DEFAULT_ALLOWANCE_MAX_TRANSACTION_FEE = Hbar.from(100);

  public NftAllowanceApproveRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(owner, "owner must not be null");
    Objects.requireNonNull(spender, "spender must not be null");
    Objects.requireNonNull(serialNumbers, "serialNumbers must not be null");
    Objects.requireNonNull(allSerialsTokenIds, "allSerialsTokenIds must not be null");
    Objects.requireNonNull(ownerKey, "ownerKey must not be null");
    if (transactionValidDuration.isZero() || transactionValidDuration.isNegative()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (owner.equals(spender)) {
      throw new IllegalArgumentException("owner and spender must be different accounts");
    }
    final Map<TokenId, List<Long>> serialNumbersCopy = new LinkedHashMap<>();
    serialNumbers.forEach(
        (tokenId, serials) -> {
          Objects.requireNonNull(tokenId, "tokenId must not be null");
          Objects.requireNonNull(serials, "serialNumbers must not be null");
          final List<Long> serialsCopy = List.copyOf(serials);
          if (serialsCopy.isEmpty()) {
            throw new IllegalArgumentException("serialNumbers must not be empty");
          }
          if (serialsCopy.stream().anyMatch(serialNumber -> serialNumber < 0)) {
            throw new IllegalArgumentException("nft serial must be non-negative");
          }
          serialNumbersCopy.put(tokenId, serialsCopy);
        });
    serialNumbers = Map.copyOf(serialNumbersCopy);
    allSerialsTokenIds = Set.copyOf(allSerialsTokenIds);
    if (serialNumbers.isEmpty() && allSerialsTokenIds.isEmpty()) {
      throw new IllegalArgumentException(
          "serialNumbers and allSerialsTokenIds must not both be empty");
    }
    if (allSerialsTokenIds.stream().anyMatch(serialNumbers::containsKey)) {
      throw new IllegalArgumentException(
          "a tokenId must not be in both serialNumbers and allSerialsTokenIds");
    }
    final int allowanceCount =
        serialNumbers.values().stream().mapToInt(List::size).sum() + allSerialsTokenIds.size();
    if (allowanceCount > MAX_ALLOWANCES) {
      throw new IllegalArgumentException(
          "must not contain more than " + MAX_ALLOWANCES + " allowances");
    }
  }

  @NonNull
  public static NftAllowanceApproveRequest of(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serialNumbers,
      @NonNull final PrivateKey ownerKey) {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(serialNumbers, "serialNumbers must not be null");
    return of(owner, spender, Map.of(tokenId, serialNumbers), ownerKey);
  }

  @NonNull
  public static NftAllowanceApproveRequest of(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final Map<TokenId, List<Long>> serialNumbers,
      @NonNull final PrivateKey ownerKey) {
    return new NftAllowanceApproveRequest(
        DEFAULT_ALLOWANCE_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        owner,
        spender,
        serialNumbers,
        Set.of(),
        ownerKey);
  }

  @NonNull
  public static NftAllowanceApproveRequest forAllSerials(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final TokenId tokenId,
      @NonNull final PrivateKey ownerKey) {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    return forAllSerials(owner, spender, List.of(tokenId), ownerKey);
  }

  @NonNull
  public static NftAllowanceApproveRequest forAllSerials(
      @NonNull final AccountId owner,
      @NonNull final AccountId spender,
      @NonNull final List<TokenId> tokenIds,
      @NonNull final PrivateKey ownerKey) {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    if (tokenIds.isEmpty()) {
      throw new IllegalArgumentException("tokenIds must not be empty");
    }
    if (new HashSet<>(tokenIds).size() != tokenIds.size()) {
      throw new IllegalArgumentException("tokenIds must not contain duplicates");
    }
    return new NftAllowanceApproveRequest(
        DEFAULT_ALLOWANCE_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        owner,
        spender,
        Map.of(),
        Set.copyOf(tokenIds),
        ownerKey);
  }
}
