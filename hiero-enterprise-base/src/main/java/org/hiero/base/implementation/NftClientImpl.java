package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hiero.base.HieroBaseException;
import org.hiero.base.HieroValidationException;
import org.hiero.base.NftClient;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.TokenAssociateRequest;
import org.hiero.base.protocol.data.TokenBurnRequest;
import org.hiero.base.protocol.data.TokenCreateRequest;
import org.hiero.base.protocol.data.TokenCreateResult;
import org.hiero.base.protocol.data.TokenDissociateRequest;
import org.hiero.base.protocol.data.TokenMintRequest;
import org.hiero.base.protocol.data.TokenMintResult;
import org.hiero.base.protocol.data.TokenTransferRequest;
import org.jspecify.annotations.NonNull;

public class NftClientImpl implements NftClient {

  private final ProtocolLayerClient client;

  private final Account operationalAccount;

  public NftClientImpl(
      @NonNull final ProtocolLayerClient client, @NonNull final Account operationalAccount) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.operationalAccount =
        Objects.requireNonNull(operationalAccount, "operationalAccount must not be null");
  }

  @Override
  public TokenId createNftType(@NonNull final String name, @NonNull final String symbol)
      throws HieroBaseException {
    return createNftType(name, symbol, operationalAccount);
  }

  @Override
  public TokenId createNftType(
      @NonNull final String name,
      @NonNull final String symbol,
      @NonNull final PrivateKey supplierKey)
      throws HieroBaseException {
    return createNftType(name, symbol, operationalAccount, supplierKey);
  }

  @Override
  public TokenId createNftType(
      @NonNull final String name,
      @NonNull final String symbol,
      @NonNull final AccountId treasuryAccountId,
      @NonNull final PrivateKey treasuryKey)
      throws HieroBaseException {
    return createNftType(
        name, symbol, treasuryAccountId, treasuryKey, operationalAccount.privateKey());
  }

  @Override
  public TokenId createNftType(
      @NonNull final String name,
      @NonNull final String symbol,
      @NonNull final AccountId treasuryAccountId,
      @NonNull final PrivateKey treasuryKey,
      @NonNull final PrivateKey supplierKey)
      throws HieroBaseException {
    try {
      final TokenCreateRequest request =
          TokenCreateRequest.of(
              name,
              symbol,
              treasuryAccountId,
              treasuryKey,
              TokenType.NON_FUNGIBLE_UNIQUE,
              supplierKey);
      final TokenCreateResult tokenCreateResult = client.executeTokenCreateTransaction(request);
      return tokenCreateResult.tokenId();
    } catch (IllegalArgumentException e) {
      throw new HieroValidationException(e.getMessage(), e);
    }
  }

  @Override
  public void associateNft(
      @NonNull final TokenId tokenId,
      @NonNull final AccountId accountId,
      @NonNull final PrivateKey accountKey)
      throws HieroBaseException {
    final TokenAssociateRequest request = TokenAssociateRequest.of(tokenId, accountId, accountKey);
    client.executeTokenAssociateTransaction(request);
  }

  @Override
  public void associateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroBaseException {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    if (tokenIds.isEmpty()) {
      throw new HieroValidationException("tokenIds must not be empty");
    }
    final TokenAssociateRequest request = TokenAssociateRequest.of(tokenIds, accountId, accountKey);
    client.executeTokenAssociateTransaction(request);
  }

  @Override
  public void dissociateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroBaseException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    final TokenDissociateRequest request =
        TokenDissociateRequest.of(tokenId, accountId, accountKey);
    client.executeTokenDissociateTransaction(request);
  }

  @Override
  public void dissociateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroBaseException {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    if (tokenIds.isEmpty()) {
      throw new HieroValidationException("tokenIds must not be empty");
    }
    final TokenDissociateRequest request =
        TokenDissociateRequest.of(tokenIds, accountId, accountKey);
    client.executeTokenDissociateTransaction(request);
  }

  @Override
  public long mintNft(@NonNull TokenId tokenId, @NonNull byte[] metadata) throws HieroBaseException {
    return mintNft(tokenId, operationalAccount.privateKey(), metadata);
  }

  @Override
  public long mintNft(
      @NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[] metadata)
      throws HieroBaseException {
    return mintNfts(tokenId, supplyKey, metadata).getFirst();
  }

  @Override
  public @NonNull List<Long> mintNfts(@NonNull TokenId tokenId, @NonNull byte[]... metadata)
      throws HieroBaseException {
    return mintNfts(tokenId, operationalAccount.privateKey(), metadata);
  }

  @Override
  public @NonNull List<Long> mintNfts(
      @NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[]... metadata)
      throws HieroBaseException {
    try {
      final TokenMintRequest request = TokenMintRequest.of(tokenId, supplyKey, metadata);
      final TokenMintResult result = client.executeMintTokenTransaction(request);
      return Collections.unmodifiableList(result.serials());
    } catch (IllegalArgumentException e) {
      throw new HieroValidationException(e.getMessage(), e);
    }
  }

  @Override
  public void burnNfts(@NonNull TokenId tokenId, @NonNull Set<Long> serialNumbers)
      throws HieroBaseException {
    burnNfts(tokenId, serialNumbers, operationalAccount.privateKey());
  }

  @Override
  public void burnNfts(
      @NonNull TokenId tokenId, @NonNull Set<Long> serialNumbers, @NonNull PrivateKey supplyKey)
      throws HieroBaseException {
    final TokenBurnRequest request = TokenBurnRequest.of(tokenId, serialNumbers, supplyKey);
    client.executeBurnTokenTransaction(request);
  }

  @Override
  public void transferNft(
      @NonNull final TokenId tokenId,
      final long serialNumber,
      @NonNull final AccountId fromAccountId,
      @NonNull final PrivateKey fromAccountKey,
      @NonNull final AccountId toAccountId)
      throws HieroBaseException {
    transferNfts(tokenId, List.of(serialNumber), fromAccountId, fromAccountKey, toAccountId);
  }

  @Override
  public void transferNfts(
      @NonNull final TokenId tokenId,
      @NonNull final List<Long> serialNumber,
      @NonNull final AccountId fromAccountId,
      @NonNull final PrivateKey fromAccountKey,
      @NonNull final AccountId toAccountId)
      throws HieroBaseException {
    try {
      final TokenTransferRequest request =
          TokenTransferRequest.of(tokenId, serialNumber, fromAccountId, toAccountId, fromAccountKey);
      client.executeTransferTransaction(request);
    } catch (IllegalArgumentException e) {
      throw new HieroValidationException(e.getMessage(), e);
    }
  }
}
