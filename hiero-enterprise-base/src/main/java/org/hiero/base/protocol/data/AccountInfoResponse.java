package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.LedgerId;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.StakingInfo;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents the result of an account info query against a consensus node.
 *
 * @param accountId the account ID
 * @param contractAccountId the Solidity/EVM account address associated with the account
 * @param deleted whether the account has been deleted
 * @param key the account key
 * @param balance the current HBAR balance
 * @param receiverSignatureRequired whether incoming transfers require a receiver signature
 * @param expirationTime the account expiration time
 * @param autoRenewPeriod the auto-renew period
 * @param accountMemo the account memo
 * @param ownedNfts the number of NFTs owned by the account
 * @param maxAutomaticTokenAssociations the max automatic token associations
 * @param aliasKey the public key alias for the account, if any
 * @param ledgerId the ledger ID the response was returned from
 * @param ethereumNonce the ethereum transaction nonce
 * @param stakingInfo staking metadata for the account, if any
 */
public record AccountInfoResponse(
    @NonNull AccountId accountId,
    @NonNull String contractAccountId,
    boolean deleted,
    @NonNull Key key,
    @NonNull Hbar balance,
    boolean receiverSignatureRequired,
    @NonNull Instant expirationTime,
    @NonNull Duration autoRenewPeriod,
    @NonNull String accountMemo,
    long ownedNfts,
    int maxAutomaticTokenAssociations,
    @Nullable PublicKey aliasKey,
    @Nullable LedgerId ledgerId,
    long ethereumNonce,
    @Nullable StakingInfo stakingInfo) {

  public AccountInfoResponse {
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(contractAccountId, "contractAccountId must not be null");
    Objects.requireNonNull(key, "key must not be null");
    Objects.requireNonNull(balance, "balance must not be null");
    Objects.requireNonNull(expirationTime, "expirationTime must not be null");
    Objects.requireNonNull(autoRenewPeriod, "autoRenewPeriod must not be null");
    Objects.requireNonNull(accountMemo, "accountMemo must not be null");
    if (balance.toTinybars() < 0) {
      throw new IllegalArgumentException("balance must be non-negative");
    }
    if (ownedNfts < 0) {
      throw new IllegalArgumentException("ownedNfts must not be negative");
    }
    if (maxAutomaticTokenAssociations < 0) {
      throw new IllegalArgumentException("maxAutomaticTokenAssociations must not be negative");
    }
  }
}
