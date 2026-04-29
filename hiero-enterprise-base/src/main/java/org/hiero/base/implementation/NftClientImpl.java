package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.CustomFee;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hiero.base.HieroException;
import org.hiero.base.NftClient;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.TokenAssociateRequest;
import org.hiero.base.protocol.data.TokenBurnRequest;
import org.hiero.base.protocol.data.TokenCreateRequest;
import org.hiero.base.protocol.data.TokenCreateResult;
import org.hiero.base.protocol.data.TokenDeleteRequest;
import org.hiero.base.protocol.data.TokenDeleteResult;
import org.hiero.base.protocol.data.TokenDissociateRequest;
import org.hiero.base.protocol.data.TokenFeeScheduleUpdateRequest;
import org.hiero.base.protocol.data.TokenFeeScheduleUpdateResult;
import org.hiero.base.protocol.data.TokenFreezeRequest;
import org.hiero.base.protocol.data.TokenFreezeResult;
import org.hiero.base.protocol.data.TokenGrantKycRequest;
import org.hiero.base.protocol.data.TokenGrantKycResult;
import org.hiero.base.protocol.data.TokenMintRequest;
import org.hiero.base.protocol.data.TokenMintResult;
import org.hiero.base.protocol.data.TokenPauseRequest;
import org.hiero.base.protocol.data.TokenPauseResult;
import org.hiero.base.protocol.data.TokenRevokeKycRequest;
import org.hiero.base.protocol.data.TokenRevokeKycResult;
import org.hiero.base.protocol.data.TokenTransferRequest;
import org.hiero.base.protocol.data.TokenUnfreezeRequest;
import org.hiero.base.protocol.data.TokenUnfreezeResult;
import org.hiero.base.protocol.data.TokenUnpauseRequest;
import org.hiero.base.protocol.data.TokenUnpauseResult;
import org.hiero.base.protocol.data.TokenWipeRequest;
import org.hiero.base.protocol.data.TokenWipeResult;
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
  public @NonNull TokenId createNftType(@NonNull String name, @NonNull String symbol)
      throws HieroException {
    return createNftType(
        name,
        symbol,
        operationalAccount.accountId(),
        operationalAccount.privateKey(),
        operationalAccount.privateKey());
  }

  @Override
  public @NonNull TokenId createNftType(
      @NonNull String name, @NonNull String symbol, @NonNull PrivateKey supplyKey)
      throws HieroException {
    return createNftType(
        name, symbol, operationalAccount.accountId(), operationalAccount.privateKey(), supplyKey);
  }

  @Override
  public @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey)
      throws HieroException {
    return createNftType(
        name, symbol, treasuryAccountId, treasuryKey, operationalAccount.privateKey());
  }

  @Override
  public @NonNull TokenId createNftType(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey,
      @NonNull PrivateKey supplyKey)
      throws HieroException {
    final TokenCreateRequest request =
        TokenCreateRequest.of(
            name, symbol, treasuryAccountId, treasuryKey, TokenType.NON_FUNGIBLE_UNIQUE, supplyKey);
    final TokenCreateResult result = client.executeTokenCreateTransaction(request);
    return result.tokenId();
  }

  @Override
  public void associateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    associateNft(Collections.singletonList(tokenId), accountId, accountKey);
  }

  @Override
  public void associateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    if (tokenIds.isEmpty()) {
      throw new IllegalArgumentException("tokenIds must not be empty");
    }
    client.executeTokenAssociateTransaction(
        TokenAssociateRequest.of(tokenIds, accountId, accountKey));
  }

  @Override
  public void dissociateNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    dissociateNft(Collections.singletonList(tokenId), accountId, accountKey);
  }

  @Override
  public void dissociateNft(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    if (tokenIds.isEmpty()) {
      throw new IllegalArgumentException("tokenIds must not be empty");
    }
    client.executeTokenDissociateTransaction(
        TokenDissociateRequest.of(tokenIds, accountId, accountKey));
  }

  @Override
  public long mintNft(@NonNull TokenId tokenId, @NonNull byte[] metadata) throws HieroException {
    return mintNft(tokenId, operationalAccount.privateKey(), metadata);
  }

  @Override
  public long mintNft(
      @NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[] metadata)
      throws HieroException {
    return mintNfts(tokenId, supplyKey, metadata).getFirst();
  }

  @Override
  public List<Long> mintNfts(@NonNull TokenId tokenId, @NonNull byte[]... metadata)
      throws HieroException {
    return mintNfts(tokenId, operationalAccount.privateKey(), metadata);
  }

  @Override
  public List<Long> mintNfts(
      @NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, @NonNull byte[]... metadata)
      throws HieroException {
    final TokenMintRequest request =
        TokenMintRequest.of(tokenId, supplyKey, Arrays.asList(metadata));
    final TokenMintResult result = client.executeMintTokenTransaction(request);
    return result.serials();
  }

  @Override
  public void burnNft(@NonNull TokenId tokenId, long serial) throws HieroException {
    burnNft(tokenId, serial, operationalAccount.privateKey());
  }

  @Override
  public void burnNft(@NonNull TokenId tokenId, long serial, @NonNull PrivateKey supplyKey)
      throws HieroException {
    burnNfts(tokenId, Collections.singleton(serial), supplyKey);
  }

  @Override
  public void burnNfts(@NonNull TokenId tokenId, @NonNull Set<Long> serials) throws HieroException {
    burnNfts(tokenId, serials, operationalAccount.privateKey());
  }

  @Override
  public void burnNfts(
      @NonNull TokenId tokenId, @NonNull Set<Long> serials, @NonNull PrivateKey supplyKey)
      throws HieroException {
    client.executeBurnTokenTransaction(TokenBurnRequest.of(tokenId, supplyKey, serials));
  }

  @Override
  public void transferNft(
      @NonNull TokenId tokenId,
      long serial,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId)
      throws HieroException {
    transferNfts(
        tokenId, Collections.singletonList(serial), fromAccountId, fromAccountKey, toAccountId);
  }

  @Override
  public void transferNfts(
      @NonNull TokenId tokenId,
      @NonNull List<Long> serials,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId)
      throws HieroException {
    final TokenTransferRequest request =
        TokenTransferRequest.of(tokenId, fromAccountId, toAccountId, fromAccountKey, serials);
    client.executeTransferTransaction(request);
  }

  @Override
  public @NonNull TokenDeleteResult deleteNft(@NonNull TokenId tokenId) throws HieroException {
    return client.executeTokenDeleteTransaction(TokenDeleteRequest.of(tokenId));
  }

  @Override
  public @NonNull TokenPauseResult pauseNft(@NonNull TokenId tokenId) throws HieroException {
    return client.executeTokenPauseTransaction(TokenPauseRequest.of(tokenId));
  }

  @Override
  public @NonNull TokenUnpauseResult unpauseNft(@NonNull TokenId tokenId) throws HieroException {
    return client.executeTokenUnpauseTransaction(TokenUnpauseRequest.of(tokenId));
  }

  @Override
  public @NonNull TokenFreezeResult freezeAccount(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException {
    return client.executeTokenFreezeTransaction(TokenFreezeRequest.of(tokenId, accountId));
  }

  @Override
  public @NonNull TokenUnfreezeResult unfreezeAccount(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException {
    return client.executeTokenUnfreezeTransaction(TokenUnfreezeRequest.of(tokenId, accountId));
  }

  @Override
  public @NonNull TokenGrantKycResult grantKyc(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException {
    return client.executeTokenGrantKycTransaction(TokenGrantKycRequest.of(tokenId, accountId));
  }

  @Override
  public @NonNull TokenRevokeKycResult revokeKyc(
      @NonNull TokenId tokenId, @NonNull AccountId accountId) throws HieroException {
    return client.executeTokenRevokeKycTransaction(TokenRevokeKycRequest.of(tokenId, accountId));
  }

  @Override
  public @NonNull TokenWipeResult wipeNft(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, long serial) throws HieroException {
    return client.executeTokenWipeTransaction(TokenWipeRequest.of(tokenId, accountId, serial));
  }

  @Override
  public @NonNull TokenFeeScheduleUpdateResult updateFeeSchedule(
      @NonNull TokenId tokenId, @NonNull List<CustomFee> customFees) throws HieroException {
    return client.executeTokenFeeScheduleUpdateTransaction(
        TokenFeeScheduleUpdateRequest.of(tokenId, customFees));
  }
}
