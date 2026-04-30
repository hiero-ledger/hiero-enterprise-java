package org.hiero.base;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.CustomFee;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.data.TokenDeleteResult;
import org.hiero.base.protocol.data.TokenFeeScheduleUpdateResult;
import org.hiero.base.protocol.data.TokenFreezeResult;
import org.hiero.base.protocol.data.TokenGrantKycResult;
import org.hiero.base.protocol.data.TokenPauseResult;
import org.hiero.base.protocol.data.TokenRevokeKycResult;
import org.hiero.base.protocol.data.TokenUnfreezeResult;
import org.hiero.base.protocol.data.TokenUnpauseResult;
import org.hiero.base.protocol.data.TokenWipeResult;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with a Hiero network. This interface provides methods for interacting
 * with Hedera Non-Fungible Tokens (NFTs).
 */
public interface NftClient {

  /**
   * Create a new NFT.
   *
   * @param name the name of the token
   * @param symbol the symbol of the token
   * @return the ID of the new token
   * @throws HieroException if the token could not be created
   */
  @NonNull TokenId createNftType(@NonNull String name, @NonNull String symbol)
      throws HieroException;

  @NonNull TokenId createNftType(
      @NonNull String name, @NonNull String symbol, @NonNull PrivateKey supplyKey)
      throws HieroException;

  @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey)
      throws HieroException;

  @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey,
      @NonNull PrivateKey supplyKey)
      throws HieroException;

  @NonNull
  default TokenId createNftType(
      @NonNull String name, @NonNull String symbol, @NonNull Account treasuryAccount)
      throws HieroException {
    Objects.requireNonNull(treasuryAccount, "treasuryAccount must not be null");
    return createNftType(name, symbol, treasuryAccount.accountId(), treasuryAccount.privateKey());
  }

  @NonNull
  default TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull Account treasuryAccount,
      @NonNull PrivateKey supplyKey)
      throws HieroException {
    Objects.requireNonNull(treasuryAccount, "treasuryAccount must not be null");
    return createNftType(
        name, symbol, treasuryAccount.accountId(), treasuryAccount.privateKey(), supplyKey);
  }

  /**
   * Associate an account with token.
   *
   * @param tokenId the ID of the token
   * @param accountId the ID of the account
   * @param accountKey the private key of the account
   * @throws HieroException if the account could not be associated with the token
   */
  void associateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  void associateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  default void associateNft(@NonNull List<TokenId> tokenIds, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    associateNft(tokenIds, account.accountId(), account.privateKey());
  }

  /**
   * Associate an account with token.
   *
   * @param tokenId the ID of the token
   * @param account the account
   * @throws HieroException if the account could not be associated with the token
   */
  default void associateNft(@NonNull TokenId tokenId, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    associateNft(tokenId, account.accountId(), account.privateKey());
  }

  /**
   * Dissociate an account with token.
   *
   * @param tokenId the ID of the token
   * @param accountId the accountId
   * @param accountKey the account privateKey
   * @throws HieroException if the account could not be associated with the token
   */
  void dissociateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  void dissociateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  default void dissociateNft(@NonNull List<TokenId> tokenIds, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    dissociateNft(tokenIds, account.accountId(), account.privateKey());
  }

  /**
   * Dissociate an account with token.
   *
   * @param tokenId the ID of the token
   * @param account the account
   * @throws HieroException if the account could not be associated with the token
   */
  default void dissociateNft(@NonNull TokenId tokenId, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    dissociateNft(tokenId, account.accountId(), account.privateKey());
  }

  /**
   * Mint a new NFT.
   *
   * @param tokenId the ID of the token
   * @param metadata the metadata for the NFT
   * @return the serial number of the new NFT
   * @throws HieroException if the NFT could not be minted
   */
  long mintNft(@NonNull TokenId tokenId, @NonNull byte[] metadata) throws HieroException;

  long mintNft(@NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[] metadata)
      throws HieroException;

  List<Long> mintNfts(@NonNull TokenId tokenId, @NonNull byte[]... metadata) throws HieroException;

  List<Long> mintNfts(
      @NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[]... metadata)
      throws HieroException;

  /**
   * Burn an NFT.
   *
   * @param tokenId the ID of the token
   * @param serial the serial number of the NFT
   * @throws HieroException if the NFT could not be burned
   */
  void burnNft(@NonNull TokenId tokenId, long serial) throws HieroException;

  void burnNft(@NonNull TokenId tokenId, long serial, @NonNull PrivateKey supplyKey)
      throws HieroException;

  void burnNfts(@NonNull TokenId tokenId, @NonNull Set<Long> serials) throws HieroException;

  void burnNfts(@NonNull TokenId tokenId, @NonNull Set<Long> serials, @NonNull PrivateKey supplyKey)
      throws HieroException;

  /**
   * Transfer an NFT.
   *
   * @param tokenId the ID of the token
   * @param serial the serial number of the NFT
   * @param fromAccountId the sender's account ID
   * @param fromAccountKey the sender's private key
   * @param toAccountId the receiver's account ID
   * @throws HieroException if the NFT could not be transferred
   */
  void transferNft(
      @NonNull TokenId tokenId,
      long serial,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId)
      throws HieroException;

  void transferNfts(
      @NonNull TokenId tokenId,
      @NonNull List<Long> serials,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId)
      throws HieroException;

  default void transferNft(
      @NonNull TokenId tokenId,
      long serial,
      @NonNull Account fromAccount,
      @NonNull AccountId toAccountId)
      throws HieroException {
    Objects.requireNonNull(fromAccount, "fromAccount must not be null");
    transferNft(tokenId, serial, fromAccount.accountId(), fromAccount.privateKey(), toAccountId);
  }

  default void transferNfts(
      @NonNull TokenId tokenId,
      @NonNull List<Long> serials,
      @NonNull Account fromAccount,
      @NonNull AccountId toAccountId)
      throws HieroException {
    Objects.requireNonNull(fromAccount, "fromAccount must not be null");
    transferNfts(tokenId, serials, fromAccount.accountId(), fromAccount.privateKey(), toAccountId);
  }

  /**
   * Deletes a token.
   *
   * @param tokenId the ID of the token to delete
   * @return the result
   * @throws HieroException if the token could not be deleted
   */
  @NonNull TokenDeleteResult deleteNft(@NonNull TokenId tokenId) throws HieroException;

  /**
   * Pauses a token.
   *
   * @param tokenId the ID of the token to pause
   * @return the result
   * @throws HieroException if the token could not be paused
   */
  @NonNull TokenPauseResult pauseNft(@NonNull TokenId tokenId) throws HieroException;

  /**
   * Unpauses a token.
   *
   * @param tokenId the ID of the token to unpause
   * @return the result
   * @throws HieroException if the token could not be unpaused
   */
  @NonNull TokenUnpauseResult unpauseNft(@NonNull TokenId tokenId) throws HieroException;

  /**
   * Freezes a token for an account.
   *
   * @param tokenId the ID of the token
   * @param accountId the ID of the account
   * @return the result
   * @throws HieroException if the transaction fails
   */
  @NonNull TokenFreezeResult freezeAccount(@NonNull TokenId tokenId, @NonNull AccountId accountId)
      throws HieroException;

  /**
   * Unfreezes a token for an account.
   *
   * @param tokenId the ID of the token
   * @param accountId the ID of the account
   * @return the result
   * @throws HieroException if the transaction fails
   */
  @NonNull TokenUnfreezeResult unfreezeAccount(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException;

  /**
   * Grants KYC for a token for an account.
   *
   * @param tokenId the ID of the token
   * @param accountId the ID of the account
   * @return the result
   * @throws HieroException if the transaction fails
   */
  @NonNull TokenGrantKycResult grantKyc(@NonNull TokenId tokenId, @NonNull AccountId accountId)
      throws HieroException;

  /**
   * Revokes KYC for a token for an account.
   *
   * @param tokenId the ID of the token
   * @param accountId the ID of the account
   * @return the result
   * @throws HieroException if the transaction fails
   */
  @NonNull TokenRevokeKycResult revokeKyc(@NonNull TokenId tokenId, @NonNull AccountId accountId)
      throws HieroException;

  /**
   * Wipes an NFT from an account.
   *
   * @param tokenId the ID of the token
   * @param accountId the ID of the account
   * @param serial the serial number
   * @return the result
   * @throws HieroException if the transaction fails
   */
  @NonNull TokenWipeResult wipeNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, long serial) throws HieroException;

  /**
   * Updates the custom fee schedule of a token.
   *
   * @param tokenId the ID of the token
   * @param customFees the new list of custom fees
   * @return the result
   * @throws HieroException if the transaction fails
   */
  @NonNull TokenFeeScheduleUpdateResult updateFeeSchedule(
      @NonNull TokenId tokenId, @NonNull List<CustomFee> customFees) throws HieroException;
}
