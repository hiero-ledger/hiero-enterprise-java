package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenType;
import java.util.List;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroException;
import org.hiero.base.TokenUpdateBuilder;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.TokenAssociateRequest;
import org.hiero.base.protocol.data.TokenBurnRequest;
import org.hiero.base.protocol.data.TokenBurnResult;
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

public class FungibleTokenClientImpl implements FungibleTokenClient {
  private final ProtocolLayerClient client;

  private final Account operationalAccount;

  public FungibleTokenClientImpl(
      @NonNull final ProtocolLayerClient client, @NonNull final Account operationalAccount) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.operationalAccount =
        Objects.requireNonNull(operationalAccount, "operationalAccount must not be null");
  }

  @Override
  public TokenId createToken(@NonNull String name, @NonNull String symbol) throws HieroException {
    return createToken(name, symbol, operationalAccount);
  }

  @Override
  public TokenId createToken(
      @NonNull String name, @NonNull String symbol, @NonNull PrivateKey supplyKey)
      throws HieroException {
    return createToken(name, symbol, operationalAccount, supplyKey);
  }

  @Override
  public TokenId createToken(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey)
      throws HieroException {
    return createToken(
        name, symbol, treasuryAccountId, treasuryKey, operationalAccount.privateKey());
  }

  @Override
  public TokenId createToken(
      @NonNull String name,
      @NonNull String symbol,
      @NonNull AccountId treasuryAccountId,
      @NonNull PrivateKey treasuryKey,
      @NonNull PrivateKey supplyKey)
      throws HieroException {
    final TokenCreateRequest request =
        TokenCreateRequest.of(
            name, symbol, treasuryAccountId, treasuryKey, TokenType.FUNGIBLE_COMMON, supplyKey);
    final TokenCreateResult result = client.executeTokenCreateTransaction(request);
    return result.tokenId();
  }

  @Override
  public void associateToken(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    final TokenAssociateRequest request = TokenAssociateRequest.of(tokenId, accountId, accountKey);
    client.executeTokenAssociateTransaction(request);
  }

  @Override
  public void associateToken(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    if (tokenIds.isEmpty()) {
      throw new IllegalArgumentException("tokenIds must not be empty");
    }
    final TokenAssociateRequest request = TokenAssociateRequest.of(tokenIds, accountId, accountKey);
    client.executeTokenAssociateTransaction(request);
  }

  @Override
  public void dissociateToken(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    final TokenDissociateRequest request =
        TokenDissociateRequest.of(tokenId, accountId, accountKey);
    client.executeTokenDissociateTransaction(request);
  }

  @Override
  public void dissociateToken(
      @NonNull List<TokenId> tokenIds, @NonNull AccountId accountId, @NonNull PrivateKey accountKey)
      throws HieroException {
    Objects.requireNonNull(tokenIds, "tokenIds must not be null");
    Objects.requireNonNull(accountId, "accountId must not be null");
    Objects.requireNonNull(accountKey, "accountKey must not be null");
    if (tokenIds.isEmpty()) {
      throw new IllegalArgumentException("tokenIds must not be empty");
    }
    final TokenDissociateRequest request =
        TokenDissociateRequest.of(tokenIds, accountId, accountKey);
    client.executeTokenDissociateTransaction(request);
  }

  @Override
  public long mintToken(@NonNull TokenId tokenId, long amount) throws HieroException {
    return mintToken(tokenId, operationalAccount.privateKey(), amount);
  }

  @Override
  public long mintToken(@NonNull TokenId tokenId, @NonNull PrivateKey supplyKey, long amount)
      throws HieroException {
    final TokenMintRequest request = TokenMintRequest.of(tokenId, supplyKey, amount);
    final TokenMintResult result = client.executeMintTokenTransaction(request);
    return result.totalSupply();
  }

  @Override
  public long burnToken(@NonNull TokenId tokenId, long amount) throws HieroException {
    return burnToken(tokenId, amount, operationalAccount.privateKey());
  }

  @Override
  public long burnToken(@NonNull TokenId tokenId, long amount, @NonNull PrivateKey supplyKey)
      throws HieroException {
    final TokenBurnRequest request = TokenBurnRequest.of(tokenId, supplyKey, amount);
    final TokenBurnResult result = client.executeBurnTokenTransaction(request);
    return result.totalSupply();
  }

  @Override
  public void transferToken(@NonNull TokenId tokenId, @NonNull AccountId toAccountId, long amount)
      throws HieroException {
    transferToken(tokenId, operationalAccount, toAccountId, amount);
  }

  @Override
  public void transferToken(
      @NonNull TokenId tokenId,
      @NonNull AccountId fromAccountId,
      @NonNull PrivateKey fromAccountKey,
      @NonNull AccountId toAccountId,
      long amount)
      throws HieroException {
    final TokenTransferRequest request =
        TokenTransferRequest.of(tokenId, fromAccountId, toAccountId, fromAccountKey, amount);
    client.executeTransferTransaction(request);
  }

  @Override
  public @NonNull TokenUpdateBuilder updateToken(@NonNull TokenId tokenId) {
    return new TokenUpdateBuilderImpl(client, tokenId);
  }

  @Override
  public @NonNull TokenDeleteResult deleteToken(@NonNull TokenId tokenId) throws HieroException {
    return client.executeTokenDeleteTransaction(TokenDeleteRequest.of(tokenId));
  }

  @Override
  public @NonNull TokenPauseResult pauseToken(@NonNull TokenId tokenId) throws HieroException {
    return client.executeTokenPauseTransaction(TokenPauseRequest.of(tokenId));
  }

  @Override
  public @NonNull TokenUnpauseResult unpauseToken(@NonNull TokenId tokenId) throws HieroException {
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
  public @NonNull TokenWipeResult wipeToken(
      @NonNull TokenId tokenId, @NonNull AccountId accountId, long amount) throws HieroException {
    return client.executeTokenWipeTransaction(TokenWipeRequest.of(tokenId, accountId, amount));
  }

  @Override
  public @NonNull TokenFeeScheduleUpdateResult updateFeeSchedule(
      @NonNull TokenId tokenId, @NonNull List<com.hedera.hashgraph.sdk.CustomFee> customFees)
      throws HieroException {
    return client.executeTokenFeeScheduleUpdateTransaction(
        TokenFeeScheduleUpdateRequest.of(tokenId, customFees));
  }
}
