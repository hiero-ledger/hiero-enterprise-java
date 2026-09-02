package org.hiero.base;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with a Hiero network. This interface provides methods for interacting
 * with Hedera NFTs, like creating and deleting NFTs. An implementation of this interface is using
 * an internal account to interact with a Hiero network. That account is the account that is used to
 * pay for the transactions that are sent to the Hedera network and called 'operator account'.
 */
public interface NftClient {

  /**
   * Create a new NFT type. That type is 'owned' by the operator account. The operator account is
   * used as suppler account and as treasury account for the NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull TokenId createNftType(@NonNull String name, @NonNull String symbol)
      throws HieroException;

  /**
   * Create a new NFT type. The operator account is used as treasury account for the NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param supplierKey the private key of the supplier account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull TokenId createNftType(
      @NonNull String name, @NonNull String symbol, @NonNull PrivateKey supplierKey)
      throws HieroException;

  /**
   * Create a new NFT type. The operator account is used as treasury account for the NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param supplierKey the private key of the supplier account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name, @NonNull String symbol, @NonNull String supplierKey)
      throws HieroException {
    Objects.requireNonNull(supplierKey, "supplierKey must not be null");
    return createNftType(name, symbol, PrivateKey.fromStringDER(supplierKey));
  }

  /**
   * Create a new NFT type. The operator account is used as supplier account for the NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccountId the ID of the treasury account
   * @param treasuryKey the private key of the treasury account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey)
      throws HieroException;

  /**
   * Create a new NFT type. The operator account is used as supplier account for the NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccountId the ID of the treasury account
   * @param treasuryKey the private key of the treasury account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull String treasuryAccountId,
      @NonNull String treasuryKey)
      throws HieroException {
    Objects.requireNonNull(treasuryAccountId, "treasuryAccountId must not be null");
    Objects.requireNonNull(treasuryKey, "treasuryKey must not be null");
    return createNftType(
        name,
        symbol,
        AccountId.fromString(treasuryAccountId),
        PrivateKey.fromStringDER(treasuryKey));
  }

  /**
   * Create a new NFT type. The operator account is used as supplier account for the NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccount the treasury account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name, @NonNull String symbol, @NonNull Account treasuryAccount)
      throws HieroException {
    Objects.requireNonNull(treasuryAccount, "treasuryAccount must not be null");
    return createNftType(name, symbol, treasuryAccount.accountId(), treasuryAccount.privateKey());
  }

  /**
   * Create a new NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccountId the ID of the treasury account
   * @param treasuryKey the private key of the treasury account
   * @param supplierKey the private key of the supplier account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey,
      @NonNull PrivateKey supplierKey)
      throws HieroException;

  /**
   * Create a new NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccountId the ID of the treasury account
   * @param treasuryKey the private key of the treasury account
   * @param supplierKey the private key of the supplier account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull String treasuryAccountId,
      @NonNull String treasuryKey,
      @NonNull String supplierKey)
      throws HieroException {
    Objects.requireNonNull(treasuryAccountId, "treasuryAccountId must not be null");
    Objects.requireNonNull(treasuryKey, "treasuryKey must not be null");
    Objects.requireNonNull(supplierKey, "supplierKey must not be null");
    return createNftType(
        name,
        symbol,
        AccountId.fromString(treasuryAccountId),
        PrivateKey.fromStringDER(treasuryKey),
        PrivateKey.fromStringDER(supplierKey));
  }

  /**
   * Create a new NFT type.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccount the treasury account
   * @param supplierKey the private key of the supplier account
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull Account treasuryAccount,
      @NonNull PrivateKey supplierKey)
      throws HieroException {
    Objects.requireNonNull(treasuryAccount, "treasuryAccount must not be null");
    return createNftType(
        name, symbol, treasuryAccount.accountId(), treasuryAccount.privateKey(), supplierKey);
  }

  /**
   * Create a new NFT type with a metadata key. The operator account is used as treasury. The
   * metadata key can authorize updates to NFT serial metadata, including after transfer out of the
   * treasury (HIP-850).
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param supplierKey the private key of the supplier account
   * @param metadataKey the private key authorized to update NFT serial metadata
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull PrivateKey supplierKey,
      @NonNull PrivateKey metadataKey)
      throws HieroException;

  /**
   * Create a new NFT type with a metadata key. The metadata key can authorize updates to NFT serial
   * metadata, including after transfer out of the treasury (HIP-850).
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccountId the ID of the treasury account
   * @param treasuryKey the private key of the treasury account
   * @param supplierKey the private key of the supplier account
   * @param metadataKey the private key authorized to update NFT serial metadata
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey,
      @NonNull PrivateKey supplierKey,
      @NonNull PrivateKey metadataKey)
      throws HieroException;

  /**
   * Create a new NFT type with a metadata key.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccountId the ID of the treasury account
   * @param treasuryKey the private key of the treasury account
   * @param supplierKey the private key of the supplier account
   * @param metadataKey the private key authorized to update NFT serial metadata
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull String treasuryAccountId,
      @NonNull String treasuryKey,
      @NonNull String supplierKey,
      @NonNull String metadataKey)
      throws HieroException {
    Objects.requireNonNull(treasuryAccountId, "treasuryAccountId must not be null");
    Objects.requireNonNull(treasuryKey, "treasuryKey must not be null");
    Objects.requireNonNull(supplierKey, "supplierKey must not be null");
    Objects.requireNonNull(metadataKey, "metadataKey must not be null");
    return createNftType(
        name,
        symbol,
        AccountId.fromString(treasuryAccountId),
        PrivateKey.fromStringDER(treasuryKey),
        PrivateKey.fromStringDER(supplierKey),
        PrivateKey.fromStringDER(metadataKey));
  }

  /**
   * Create a new NFT type with a metadata key.
   *
   * @param name the name of the NFT type
   * @param symbol the symbol of the NFT type
   * @param treasuryAccount the treasury account
   * @param supplierKey the private key of the supplier account
   * @param metadataKey the private key authorized to update NFT serial metadata
   * @return the ID of the new NFT type
   * @throws HieroException if the NFT type could not be created
   */
  @NonNull
  default TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull Account treasuryAccount,
      @NonNull PrivateKey supplierKey,
      @NonNull PrivateKey metadataKey)
      throws HieroException {
    Objects.requireNonNull(treasuryAccount, "treasuryAccount must not be null");
    return createNftType(
        name,
        symbol,
        treasuryAccount.accountId(),
        treasuryAccount.privateKey(),
        supplierKey,
        metadataKey);
  }

  /**
   * Associate an account with an NFT type. If an account is associated with an NFT type, the
   * account can hold NFTs of that type. Otherwise, the account cannot hold NFTs of that type and
   * tranfer NFTs of that type will fail.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account
   * @param accountKey the private key of the account
   * @throws HieroException if the account could not be associated with the NFT type
   */
  void associateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  /**
   * Associate an account with an NFT type. If an account is associated with an NFT type, the
   * account can hold NFTs of that type. Otherwise, the account cannot hold NFTs of that type and
   * tranfer NFTs of that type will fail.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account
   * @param accountKey the private key of the account
   * @throws HieroException if the account could not be associated with the NFT type
   */
  default void associateNft(
      @NonNull String tokenId, @NonNull String accountId, @NonNull String accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    associateNft(
        TokenId.fromString(tokenId),
        AccountId.fromString(accountId),
        PrivateKey.fromStringDER(accountKey));
  }

  /**
   * Associate an account with an NFT type. If an account is associated with an NFT type, the
   * account can hold NFTs of that type. Otherwise, the account cannot hold NFTs of that type and
   * tranfer NFTs of that type will fail.
   *
   * @param tokenId the ID of the NFT type
   * @param account the account
   * @throws HieroException if the account could not be associated with the NFT type
   */
  default void associateNft(@NonNull TokenId tokenId, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    associateNft(tokenId, account.accountId(), account.privateKey());
  }

  /**
   * Associate an account with an NFT type. If an account is associated with an NFT type, the
   * account can hold NFTs of that type. Otherwise, the account cannot hold NFTs of that type and
   * tranfer NFTs of that type will fail.
   *
   * @param tokenIds the List of ID for NFT type
   * @param accountId the accountId
   * @param accountKey the account privateKey
   * @throws HieroException if the account could not be associated with the NFT type
   */
  void associateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  /**
   * Associate an account with an NFT type. If an account is associated with an NFT type, the
   * account can hold NFTs of that type. Otherwise, the account cannot hold NFTs of that type and
   * tranfer NFTs of that type will fail.
   *
   * @param tokenIds the List of ID for NFT type
   * @param account the account
   * @throws HieroException if the account could not be associated with the NFT type
   */
  default void associateNft(@NonNull List<TokenId> tokenIds, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "accountId must not be null");
    associateNft(tokenIds, account.accountId(), account.privateKey());
  }
  ;

  /**
   * Dissociate an account with an NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the accountId
   * @param accountKey the account privateKey
   * @throws HieroException if the account could not be associated with the NFT type
   */
  void dissociateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  /**
   * Dissociate an account with an NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the accountId
   * @param accountKey the account privateKey
   * @throws HieroException if the account could not be associated with the NFT type
   */
  default void dissociateNft(
      @NonNull String tokenId, @NonNull String accountId, @NonNull String accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    dissociateNft(
        TokenId.fromString(tokenId),
        AccountId.fromString(accountId),
        PrivateKey.fromStringDER(accountKey));
  }
  ;

  /**
   * Dissociate an account with an NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param account the account
   * @throws HieroException if the account could not be associated with the NFT type
   */
  default void dissociateNft(@NonNull TokenId tokenId, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "accountId must not be null");
    dissociateNft(tokenId, account.accountId(), account.privateKey());
  }
  ;

  /**
   * Dissociate an account with an NFT type.
   *
   * @param tokenIds the List of ID for NFT type
   * @param accountId the accountId
   * @param accountKey the account privateKey
   * @throws HieroException if the account could not be associated with the NFT type
   */
  void dissociateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException;

  /**
   * Dissociate an account with an NFT type.
   *
   * @param tokenIds the List of ID for NFT type
   * @param account the account
   * @throws HieroException if the account could not be associated with the NFT type
   */
  default void dissociateNft(@NonNull List<TokenId> tokenIds, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "accountId must not be null");
    dissociateNft(tokenIds, account.accountId(), account.privateKey());
  }
  ;

  /**
   * Freezes an account for the given NFT type. The operator account key is used as the freeze key.
   * A frozen account cannot send or receive NFTs of that type.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account to freeze
   * @throws HieroException if the account could not be frozen for the NFT type
   */
  void freezeNft(@NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException;

  /**
   * Freezes an account for the given NFT type. Must be signed by the token freeze key.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account to freeze
   * @param freezeKey the freeze key of the NFT type
   * @throws HieroException if the account could not be frozen for the NFT type
   */
  void freezeNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey freezeKey)
      throws HieroException;

  /**
   * Freezes an account for the given NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account to freeze
   * @param freezeKey the freeze key of the NFT type
   * @throws HieroException if the account could not be frozen for the NFT type
   */
  default void freezeNft(
      @NonNull String tokenId, @NonNull String accountId, @NonNull String freezeKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(freezeKey, "freezeKey must not be null");
    freezeNft(
        TokenId.fromString(tokenId),
        AccountId.fromString(accountId),
        PrivateKey.fromStringDER(freezeKey));
  }

  /**
   * Freezes an account for the given NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param account the account to freeze
   * @throws HieroException if the account could not be frozen for the NFT type
   */
  default void freezeNft(@NonNull TokenId tokenId, @NonNull Account account) throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    freezeNft(tokenId, account.accountId(), account.privateKey());
  }

  /**
   * Unfreezes an account for the given NFT type. The operator account key is used as the freeze
   * key.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account to unfreeze
   * @throws HieroException if the account could not be unfrozen for the NFT type
   */
  void unfreezeNft(@NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException;

  /**
   * Unfreezes an account for the given NFT type. Must be signed by the token freeze key.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account to unfreeze
   * @param freezeKey the freeze key of the NFT type
   * @throws HieroException if the account could not be unfrozen for the NFT type
   */
  void unfreezeNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey freezeKey)
      throws HieroException;

  /**
   * Unfreezes an account for the given NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param accountId the ID of the account to unfreeze
   * @param freezeKey the freeze key of the NFT type
   * @throws HieroException if the account could not be unfrozen for the NFT type
   */
  default void unfreezeNft(
      @NonNull String tokenId, @NonNull String accountId, @NonNull String freezeKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(freezeKey, "freezeKey must not be null");
    unfreezeNft(
        TokenId.fromString(tokenId),
        AccountId.fromString(accountId),
        PrivateKey.fromStringDER(freezeKey));
  }

  /**
   * Unfreezes an account for the given NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @param account the account to unfreeze
   * @throws HieroException if the account could not be unfrozen for the NFT type
   */
  default void unfreezeNft(@NonNull TokenId tokenId, @NonNull Account account)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    unfreezeNft(tokenId, account.accountId(), account.privateKey());
  }

  /**
   * Mint a new NFT of the given type. The NFT is minted by the operator account. The operator
   * account is used as supply account for the NFT.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFT
   * @return the serial number of the new NFT
   * @throws HieroException if the NFT could not be minted
   */
  long mintNft(@NonNull TokenId tokenId, @NonNull byte[] metadata) throws HieroException;

  /**
   * Mint a new NFT of the given type. The NFT is minted by the operator account. The operator
   * account is used as supply account for the NFT.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFT
   * @return the serial number of the new NFT
   * @throws HieroException if the NFT could not be minted
   */
  default long mintNft(@NonNull String tokenId, @NonNull byte[] metadata) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    return mintNft(TokenId.fromString(tokenId), metadata);
  }

  /**
   * Mint a new NFT of the given type.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFT
   * @param supplyKey the private key of the supply account
   * @return the serial number of the new NFT
   * @throws HieroException if the NFT could not be minted
   */
  long mintNft(@NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[] metadata)
      throws HieroException;

  /**
   * Mint a new NFT of the given type.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFT
   * @param supplyKey the private key of the supply account
   * @return the serial number of the new NFT
   * @throws HieroException if the NFT could not be minted
   */
  default long mintNft(@NonNull String tokenId, @NonNull String supplyKey, @NonNull byte[] metadata)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(supplyKey, "supplyKey must not be null");
    return mintNft(TokenId.fromString(tokenId), PrivateKey.fromStringDER(supplyKey), metadata);
  }

  /**
   * Mint new NFTs of the given type. The NFTs are minted by the operator account. The operator
   * account is used as supply account for the NFTs.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFTs
   * @return the serial numbers of the new NFTs
   * @throws HieroException if the NFTs could not be minted
   */
  @NonNull List<Long> mintNfts(@NonNull TokenId tokenId, @NonNull byte[]... metadata)
      throws HieroException;

  /**
   * Mint new NFTs of the given type. The NFTs are minted by the operator account. The operator
   * account is used as supply account for the NFTs.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFTs
   * @return the serial numbers of the new NFTs
   * @throws HieroException if the NFTs could not be minted
   */
  @NonNull
  default List<Long> mintNfts(@NonNull String tokenId, @NonNull byte[]... metadata)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    return mintNfts(TokenId.fromString(tokenId), metadata);
  }

  /**
   * Mint new NFTs of the given type.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFTs
   * @param supplyKey the private key of the supply account
   * @return the serial numbers of the new NFTs
   * @throws HieroException if the NFTs could not be minted
   */
  @NonNull List<Long> mintNfts(
      @NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[]... metadata)
      throws HieroException;

  /**
   * Mint new NFTs of the given type.
   *
   * @param tokenId the ID of the NFT type
   * @param metadata the metadata of the NFTs
   * @param supplyKey the private key of the supply account
   * @return the serial numbers of the new NFTs
   * @throws HieroException if the NFTs could not be minted
   */
  @NonNull
  default List<Long> mintNfts(
      @NonNull String tokenId, @NonNull String supplyKey, @NonNull byte[]... metadata)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(supplyKey, "supplyKey must not be null");
    return mintNfts(TokenId.fromString(tokenId), PrivateKey.fromStringDER(supplyKey), metadata);
  }

  /**
   * Burn an NFT.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @return total supply for the NFT type after the burn
   * @throws HieroException if the NFT could not be burned
   */
  default long burnNft(@NonNull TokenId tokenId, long serialNumber) throws HieroException {
    return burnNfts(tokenId, Set.of(serialNumber));
  }

  /**
   * Burn an NFT.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param supplyKey the private key of the supply account
   * @return total supply for the NFT type after the burn
   * @throws HieroException if the NFT could not be burned
   */
  default long burnNft(@NonNull TokenId tokenId, long serialNumber, @NonNull PrivateKey supplyKey)
      throws HieroException {
    return burnNfts(tokenId, Set.of(serialNumber), supplyKey);
  }

  /**
   * Burn NFTs.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @return total supply for the NFT type after the burn
   * @throws HieroException if the NFTs could not be burned
   */
  long burnNfts(@NonNull TokenId tokenId, @NonNull Set<Long> serialNumbers) throws HieroException;

  /**
   * Burn NFTs.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @param supplyKey the private key of the supply account
   * @return total supply for the NFT type after the burn
   * @throws HieroException if the NFTs could not be burned
   */
  long burnNfts(
      @NonNull TokenId tokenId, @NonNull Set<Long> serialNumbers, @NonNull PrivateKey supplyKey)
      throws HieroException;

  /**
   * Wipes an NFT from an account. The operator account key is used as the wipe key. The account
   * must not be the token treasury. Wiping burns the NFT and decreases total supply.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param accountId the account to wipe the NFT from
   * @return total supply for the NFT type after the wipe
   * @throws HieroException if the NFT could not be wiped
   */
  default long wipeNft(@NonNull TokenId tokenId, long serialNumber, @NonNull AccountId accountId)
      throws HieroException {
    return wipeNfts(tokenId, Set.of(serialNumber), accountId);
  }

  /**
   * Wipes an NFT from an account. Must be signed by the token wipe key. The account must not be the
   * token treasury.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param accountId the account to wipe the NFT from
   * @param wipeKey the wipe key of the NFT type
   * @return total supply for the NFT type after the wipe
   * @throws HieroException if the NFT could not be wiped
   */
  default long wipeNft(
      @NonNull TokenId tokenId,
      long serialNumber,
      @NonNull AccountId accountId,
      @NonNull PrivateKey wipeKey)
      throws HieroException {
    return wipeNfts(tokenId, Set.of(serialNumber), accountId, wipeKey);
  }

  /**
   * Wipes an NFT from an account.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param account the account to wipe the NFT from
   * @param wipeKey the wipe key of the NFT type
   * @return total supply for the NFT type after the wipe
   * @throws HieroException if the NFT could not be wiped
   */
  default long wipeNft(
      @NonNull TokenId tokenId,
      long serialNumber,
      @NonNull Account account,
      @NonNull PrivateKey wipeKey)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    return wipeNft(tokenId, serialNumber, account.accountId(), wipeKey);
  }

  /**
   * Wipes NFTs from an account. The operator account key is used as the wipe key. The account must
   * not be the token treasury. Wiping burns the NFTs and decreases total supply.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @param accountId the account to wipe the NFTs from
   * @return total supply for the NFT type after the wipe
   * @throws HieroException if the NFTs could not be wiped
   */
  long wipeNfts(
      @NonNull TokenId tokenId, @NonNull Set<Long> serialNumbers, @NonNull AccountId accountId)
      throws HieroException;

  /**
   * Wipes NFTs from an account. Must be signed by the token wipe key. The account must not be the
   * token treasury.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @param accountId the account to wipe the NFTs from
   * @param wipeKey the wipe key of the NFT type
   * @return total supply for the NFT type after the wipe
   * @throws HieroException if the NFTs could not be wiped
   */
  long wipeNfts(
      @NonNull TokenId tokenId,
      @NonNull Set<Long> serialNumbers,
      @NonNull AccountId accountId,
      @NonNull PrivateKey wipeKey)
      throws HieroException;

  /**
   * Wipes NFTs from an account.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @param account the account to wipe the NFTs from
   * @param wipeKey the wipe key of the NFT type
   * @return total supply for the NFT type after the wipe
   * @throws HieroException if the NFTs could not be wiped
   */
  default long wipeNfts(
      @NonNull TokenId tokenId,
      @NonNull Set<Long> serialNumbers,
      @NonNull Account account,
      @NonNull PrivateKey wipeKey)
      throws HieroException {
    Objects.requireNonNull(account, "account must not be null");
    return wipeNfts(tokenId, serialNumbers, account.accountId(), wipeKey);
  }

  /**
   * Pauses an NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @throws HieroException if the NFT type could not be paused
   */
  void pauseNft(@NonNull TokenId tokenId) throws HieroException;

  void pauseNft(@NonNull TokenId tokenId, @NonNull PrivateKey pauseKey) throws HieroException;

  /**
   * Unpauses an NFT type.
   *
   * @param tokenId the ID of the NFT type
   * @throws HieroException if the NFT type could not be unpaused
   */
  void unpauseNft(@NonNull TokenId tokenId) throws HieroException;

  void unpauseNft(@NonNull TokenId tokenId, @NonNull PrivateKey pauseKey) throws HieroException;

  default void pauseNft(@NonNull String tokenId) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    pauseNft(TokenId.fromString(tokenId));
  }

  default void pauseNft(@NonNull String tokenId, @NonNull String pauseKey) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(pauseKey, "pauseKey must not be null");

    pauseNft(TokenId.fromString(tokenId), PrivateKey.fromString(pauseKey));
  }

  default void unpauseNft(@NonNull String tokenId) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    unpauseNft(TokenId.fromString(tokenId));
  }

  default void unpauseNft(@NonNull String tokenId, @NonNull String pauseKey) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(pauseKey, "pauseKey must not be null");

    unpauseNft(TokenId.fromString(tokenId), PrivateKey.fromString(pauseKey));
  }

  /**
   * Transfer an NFT to another account.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param fromAccountId the ID of the account that holds the NFT
   * @param fromAccountKey the private key of the account that holds the NFT
   * @param toAccountId the ID of the account that should receive the NFT
   * @throws HieroException if the NFT could not be transferred
   */
  void transferNft(
      @NonNull TokenId tokenId,
      long serialNumber,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId)
      throws HieroException;

  /**
   * Transfer an NFT to another account.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param fromAccount the account that holds the NFT
   * @param toAccountId the ID of the account that should receive the NFT
   * @throws HieroException if the NFT could not be transferred
   */
  default void transferNft(
      @NonNull TokenId tokenId,
      long serialNumber,
      @NonNull Account fromAccount,
      @NonNull AccountId toAccountId)
      throws HieroException {
    Objects.requireNonNull(fromAccount, "fromAccount must not be null");
    transferNft(
        tokenId, serialNumber, fromAccount.accountId(), fromAccount.privateKey(), toAccountId);
  }

  /**
   * Transfer NFTs to another account.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @param fromAccountId the ID of the account that holds the NFTs
   * @param fromAccountKey the private key of the account that holds the NFTs
   * @param toAccountId the ID of the account that should receive the NFTs
   * @throws HieroException if the NFTs could not be transferred
   */
  void transferNfts(
      @NonNull TokenId tokenId,
      @NonNull List<Long> serialNumbers,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId)
      throws HieroException;

  /**
   * Transfer NFTs to another account.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers of the NFTs
   * @param fromAccount the account that holds the NFTs
   * @param toAccountId the ID of the account that should receive the NFTs
   * @throws HieroException if the NFTs could not be transferred
   */
  default void transferNfts(
      @NonNull TokenId tokenId,
      @NonNull List<Long> serialNumbers,
      @NonNull Account fromAccount,
      @NonNull AccountId toAccountId)
      throws HieroException {
    Objects.requireNonNull(fromAccount, "fromAccount must not be null");
    transferNfts(
        tokenId, serialNumbers, fromAccount.accountId(), fromAccount.privateKey(), toAccountId);
  }

  /**
   * Updates an NFT type (token class) name and symbol. The operator account key is used as the
   * admin key. The NFT type must have been created with that key as admin (the default for {@link
   * #createNftType} when the operator is the treasury).
   *
   * @param tokenId the ID of the NFT type to update
   * @param name the new name of the NFT type
   * @param symbol the new symbol of the NFT type
   * @throws HieroException if the NFT type could not be updated
   */
  void updateNftType(@NonNull TokenId tokenId, @NonNull String name, @NonNull String symbol)
      throws HieroException;

  /**
   * Updates an NFT type (token class) name and symbol. Must be signed by the admin key that was set
   * when the NFT type was created.
   *
   * @param tokenId the ID of the NFT type to update
   * @param name the new name of the NFT type
   * @param symbol the new symbol of the NFT type
   * @param adminKey the admin private key of the NFT type
   * @throws HieroException if the NFT type could not be updated
   */
  void updateNftType(
      @NonNull TokenId tokenId,
      @NonNull String name,
      @NonNull String symbol,
      @NonNull PrivateKey adminKey)
      throws HieroException;

  /**
   * Updates an NFT type (token class) name and symbol.
   *
   * @param tokenId the ID of the NFT type to update
   * @param name the new name of the NFT type
   * @param symbol the new symbol of the NFT type
   * @param adminKey the admin private key of the NFT type
   * @throws HieroException if the NFT type could not be updated
   */
  default void updateNftType(
      @NonNull String tokenId,
      @NonNull String name,
      @NonNull String symbol,
      @NonNull String adminKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    updateNftType(TokenId.fromString(tokenId), name, symbol, PrivateKey.fromStringDER(adminKey));
  }

  /**
   * Updates an NFT type (token class) name and symbol. The operator account key is used as the
   * admin key.
   *
   * @param tokenId the ID of the NFT type to update
   * @param name the new name of the NFT type
   * @param symbol the new symbol of the NFT type
   * @throws HieroException if the NFT type could not be updated
   */
  default void updateNftType(@NonNull String tokenId, @NonNull String name, @NonNull String symbol)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    updateNftType(TokenId.fromString(tokenId), name, symbol);
  }

  /**
   * Updates the metadata of a single NFT serial. The operator account key is used as the metadata
   * key (or supply key while the NFT is held in treasury; see HIP-850).
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param metadata the new metadata (at most 100 bytes)
   * @throws HieroException if the NFT metadata could not be updated
   */
  default void updateNftMetadata(
      @NonNull TokenId tokenId, long serialNumber, @NonNull byte[] metadata) throws HieroException {
    updateNftsMetadata(tokenId, List.of(serialNumber), metadata);
  }

  /**
   * Updates the metadata of a single NFT serial. Must be signed by the token metadata key, or by
   * the supply key while the NFT is held in the treasury (HIP-850).
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumber the serial number of the NFT
   * @param metadataKey the metadata key (or supply key for treasury-held NFTs)
   * @param metadata the new metadata (at most 100 bytes)
   * @throws HieroException if the NFT metadata could not be updated
   */
  default void updateNftMetadata(
      @NonNull TokenId tokenId,
      long serialNumber,
      @NonNull PrivateKey metadataKey,
      @NonNull byte[] metadata)
      throws HieroException {
    updateNftsMetadata(tokenId, List.of(serialNumber), metadataKey, metadata);
  }

  /**
   * Updates the metadata of NFT serials. The operator account key is used as the metadata key (or
   * supply key while the NFTs are held in treasury; see HIP-850). At most 10 serials may be updated
   * in one call.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers to update
   * @param metadata the new metadata (at most 100 bytes)
   * @throws HieroException if the NFT metadata could not be updated
   */
  void updateNftsMetadata(
      @NonNull TokenId tokenId, @NonNull List<Long> serialNumbers, @NonNull byte[] metadata)
      throws HieroException;

  /**
   * Updates the metadata of NFT serials. Must be signed by the token metadata key, or by the supply
   * key while the NFTs are held in the treasury (HIP-850). At most 10 serials may be updated in one
   * call.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers to update
   * @param metadataKey the metadata key (or supply key for treasury-held NFTs)
   * @param metadata the new metadata (at most 100 bytes)
   * @throws HieroException if the NFT metadata could not be updated
   */
  void updateNftsMetadata(
      @NonNull TokenId tokenId,
      @NonNull List<Long> serialNumbers,
      @NonNull PrivateKey metadataKey,
      @NonNull byte[] metadata)
      throws HieroException;

  /**
   * Updates the metadata of NFT serials.
   *
   * @param tokenId the ID of the NFT type
   * @param serialNumbers the serial numbers to update
   * @param metadataKey the metadata key (or supply key for treasury-held NFTs)
   * @param metadata the new metadata (at most 100 bytes)
   * @throws HieroException if the NFT metadata could not be updated
   */
  default void updateNftsMetadata(
      @NonNull String tokenId,
      @NonNull List<Long> serialNumbers,
      @NonNull String metadataKey,
      @NonNull byte[] metadata)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(metadataKey, "metadataKey must not be null");
    updateNftsMetadata(
        TokenId.fromString(tokenId),
        serialNumbers,
        PrivateKey.fromStringDER(metadataKey),
        metadata);
  }

  /**
   * Deletes an NFT type (token class). All NFTs of that type must have been burned first. The
   * operator account key is used as the admin key. The NFT type must have been created with that
   * key as admin (the default for {@link #createNftType} when the operator is the treasury).
   *
   * @param tokenId the ID of the NFT type to delete
   * @throws HieroException if the NFT type could not be deleted
   */
  void deleteNftType(@NonNull TokenId tokenId) throws HieroException;

  /**
   * Deletes an NFT type (token class). All NFTs of that type must have been burned first. Must be
   * signed by the admin key that was set when the NFT type was created.
   *
   * @param tokenId the ID of the NFT type to delete
   * @param adminKey the admin private key of the NFT type
   * @throws HieroException if the NFT type could not be deleted
   */
  void deleteNftType(@NonNull TokenId tokenId, @NonNull PrivateKey adminKey) throws HieroException;

  /**
   * Deletes an NFT type (token class). All NFTs of that type must have been burned first.
   *
   * @param tokenId the ID of the NFT type to delete
   * @param adminKey the admin private key of the NFT type
   * @throws HieroException if the NFT type could not be deleted
   */
  default void deleteNftType(@NonNull String tokenId, @NonNull String adminKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    deleteNftType(TokenId.fromString(tokenId), PrivateKey.fromStringDER(adminKey));
  }

  default void deleteNftType(@NonNull String tokenId) throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    deleteNftType(TokenId.fromString(tokenId));
  }
}
