package org.hiero.base.test;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenType;
import java.util.List;
import org.hiero.base.data.Account;
import org.hiero.base.implementation.ProtocolLayerClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.AccountCreateRequest;
import org.hiero.base.protocol.data.AccountCreateResult;
import org.hiero.base.protocol.data.TokenAssociateRequest;
import org.hiero.base.protocol.data.TokenBurnRequest;
import org.hiero.base.protocol.data.TokenCreateRequest;
import org.hiero.base.protocol.data.TokenCreateResult;
import org.hiero.base.protocol.data.TokenDeleteRequest;
import org.hiero.base.protocol.data.TokenDeleteResult;
import org.hiero.base.protocol.data.TokenMintRequest;
import org.hiero.base.protocol.data.TokenMintResult;
import org.hiero.base.protocol.data.TokenTransferRequest;
import org.hiero.base.protocol.data.TokenUpdateNftsRequest;
import org.hiero.base.protocol.data.TokenUpdateNftsResult;
import org.hiero.base.protocol.data.TokenUpdateRequest;
import org.hiero.base.protocol.data.TokenUpdateResult;
import org.hiero.base.protocol.data.TokenWipeRequest;
import org.hiero.base.protocol.data.TokenWipeResult;
import org.hiero.base.test.config.HieroTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProtocolLayerClientTokenTests {

  private static HieroTestContext hieroTestContext;

  private static ProtocolLayerClient protocolLayerClient;

  @BeforeAll
  static void init() {
    hieroTestContext = new HieroTestContext();
    protocolLayerClient = new ProtocolLayerClientImpl(hieroTestContext);
  }

  @Test
  void testBurnNft() throws Exception {
    // given
    final TokenCreateRequest tokenCreateRequest =
        TokenCreateRequest.of(
            "Test NFT",
            "TST",
            TokenType.NON_FUNGIBLE_UNIQUE,
            hieroTestContext.getOperatorAccount());
    final TokenCreateResult tokenCreateResult =
        protocolLayerClient.executeTokenCreateTransaction(tokenCreateRequest);
    final TokenId tokenId = tokenCreateResult.tokenId();

    final TokenMintRequest tokenMintRequest =
        TokenMintRequest.of(
            tokenId,
            hieroTestContext.getOperatorAccount().privateKey(),
            "https://example.com/metadata");
    final TokenMintResult tokenMintResult =
        protocolLayerClient.executeMintTokenTransaction(tokenMintRequest);
    final Long serial = tokenMintResult.serials().get(0);

    // when
    final TokenBurnRequest tokenBurnRequest =
        TokenBurnRequest.of(tokenId, serial, hieroTestContext.getOperatorAccount().privateKey());

    // then
    Assertions.assertDoesNotThrow(
        () -> protocolLayerClient.executeBurnTokenTransaction(tokenBurnRequest));
  }

  @Test
  void testWipeNft() throws Exception {
    // given — create wipeable NFT type, mint, transfer off treasury, then wipe
    final Account operator = hieroTestContext.getOperatorAccount();
    final TokenCreateRequest tokenCreateRequest =
        TokenCreateRequest.of(
            "Wipe NFT",
            "WIP",
            operator.accountId(),
            operator.privateKey(),
            TokenType.NON_FUNGIBLE_UNIQUE,
            operator.privateKey(),
            null,
            operator.privateKey());
    final TokenId tokenId =
        protocolLayerClient.executeTokenCreateTransaction(tokenCreateRequest).tokenId();

    final Long serial =
        protocolLayerClient
            .executeMintTokenTransaction(
                TokenMintRequest.of(
                    tokenId, operator.privateKey(), "https://example.com/wipe-metadata"))
            .serials()
            .get(0);

    final AccountCreateResult accountCreateResult =
        protocolLayerClient.executeAccountCreateTransaction(AccountCreateRequest.of(Hbar.from(2)));
    final Account holder = accountCreateResult.newAccount();

    protocolLayerClient.executeTokenAssociateTransaction(
        TokenAssociateRequest.of(tokenId, holder.accountId(), holder.privateKey()));
    protocolLayerClient.executeTransferTransaction(
        TokenTransferRequest.of(
            tokenId,
            List.of(serial),
            operator.accountId(),
            holder.accountId(),
            operator.privateKey()));

    // when
    final TokenWipeRequest wipeRequest =
        TokenWipeRequest.of(tokenId, holder.accountId(), serial, operator.privateKey());
    final TokenWipeResult wipeResult = protocolLayerClient.executeWipeTokenTransaction(wipeRequest);

    // then
    Assertions.assertNotNull(wipeResult);
    Assertions.assertNotNull(wipeResult.transactionId());
    Assertions.assertNotNull(wipeResult.totalSupply());
  }

  @Test
  void testDeleteNftType() throws Exception {
    // given — create an empty NFT type (no minted serials)
    final TokenCreateRequest tokenCreateRequest =
        TokenCreateRequest.of(
            "Delete NFT",
            "DEL",
            TokenType.NON_FUNGIBLE_UNIQUE,
            hieroTestContext.getOperatorAccount());
    final TokenCreateResult tokenCreateResult =
        protocolLayerClient.executeTokenCreateTransaction(tokenCreateRequest);
    final TokenId tokenId = tokenCreateResult.tokenId();

    // when
    final TokenDeleteRequest tokenDeleteRequest =
        TokenDeleteRequest.of(tokenId, hieroTestContext.getOperatorAccount().privateKey());
    final TokenDeleteResult tokenDeleteResult =
        protocolLayerClient.executeTokenDeleteTransaction(tokenDeleteRequest);

    // then
    Assertions.assertNotNull(tokenDeleteResult);
    Assertions.assertNotNull(tokenDeleteResult.transactionId());
  }

  @Test
  void testUpdateNftType() throws Exception {
    // given
    final TokenCreateRequest tokenCreateRequest =
        TokenCreateRequest.of(
            "Update NFT",
            "UPD",
            TokenType.NON_FUNGIBLE_UNIQUE,
            hieroTestContext.getOperatorAccount());
    final TokenCreateResult tokenCreateResult =
        protocolLayerClient.executeTokenCreateTransaction(tokenCreateRequest);
    final TokenId tokenId = tokenCreateResult.tokenId();

    // when
    final TokenUpdateRequest tokenUpdateRequest =
        TokenUpdateRequest.of(
            tokenId, hieroTestContext.getOperatorAccount().privateKey(), "Updated NFT", "UNFT");
    final TokenUpdateResult tokenUpdateResult =
        protocolLayerClient.executeTokenUpdateTransaction(tokenUpdateRequest);

    // then
    Assertions.assertNotNull(tokenUpdateResult);
    Assertions.assertNotNull(tokenUpdateResult.transactionId());
    Assertions.assertEquals(Status.SUCCESS, tokenUpdateResult.status());
  }

  @Test
  void testUpdateNftMetadata() throws Exception {
    // given — mint stays in treasury so supply key can update metadata (HIP-850)
    final TokenCreateRequest tokenCreateRequest =
        TokenCreateRequest.of(
            "Meta NFT",
            "META",
            TokenType.NON_FUNGIBLE_UNIQUE,
            hieroTestContext.getOperatorAccount());
    final TokenCreateResult tokenCreateResult =
        protocolLayerClient.executeTokenCreateTransaction(tokenCreateRequest);
    final TokenId tokenId = tokenCreateResult.tokenId();

    final TokenMintRequest tokenMintRequest =
        TokenMintRequest.of(
            tokenId, hieroTestContext.getOperatorAccount().privateKey(), "https://example.com/old");
    final TokenMintResult tokenMintResult =
        protocolLayerClient.executeMintTokenTransaction(tokenMintRequest);
    final Long serial = tokenMintResult.serials().get(0);

    // when
    final byte[] updatedMetadata = "https://example.com/new".getBytes();
    final TokenUpdateNftsRequest updateRequest =
        TokenUpdateNftsRequest.of(
            tokenId, serial, updatedMetadata, hieroTestContext.getOperatorAccount().privateKey());
    final TokenUpdateNftsResult updateResult =
        protocolLayerClient.executeTokenUpdateNftsTransaction(updateRequest);

    // then
    Assertions.assertNotNull(updateResult);
    Assertions.assertNotNull(updateResult.transactionId());
    Assertions.assertEquals(Status.SUCCESS, updateResult.status());
  }

  @Test
  void testUpdateNftMetadataWithMetadataKey() throws Exception {
    // given — create with a dedicated metadata key, then transfer out of treasury
    final PrivateKey metadataKey = PrivateKey.generateECDSA();
    final Account operator = hieroTestContext.getOperatorAccount();
    final TokenCreateRequest tokenCreateRequest =
        TokenCreateRequest.of(
            "MetaKey NFT",
            "MKEY",
            operator.accountId(),
            operator.privateKey(),
            TokenType.NON_FUNGIBLE_UNIQUE,
            operator.privateKey(),
            metadataKey);
    final TokenCreateResult tokenCreateResult =
        protocolLayerClient.executeTokenCreateTransaction(tokenCreateRequest);
    final TokenId tokenId = tokenCreateResult.tokenId();

    final TokenMintRequest tokenMintRequest =
        TokenMintRequest.of(tokenId, operator.privateKey(), "https://example.com/old");
    final TokenMintResult tokenMintResult =
        protocolLayerClient.executeMintTokenTransaction(tokenMintRequest);
    final Long serial = tokenMintResult.serials().get(0);

    final Account receiver =
        protocolLayerClient
            .executeAccountCreateTransaction(AccountCreateRequest.of(Hbar.from(1L)))
            .newAccount();
    protocolLayerClient.executeTokenAssociateTransaction(
        TokenAssociateRequest.of(tokenId, receiver.accountId(), receiver.privateKey()));
    protocolLayerClient.executeTransferTransaction(
        TokenTransferRequest.of(
            tokenId, serial, operator.accountId(), receiver.accountId(), operator.privateKey()));

    // when — update using the metadata key after transfer out of treasury
    final byte[] updatedMetadata = "https://example.com/new".getBytes();
    final TokenUpdateNftsRequest updateRequest =
        TokenUpdateNftsRequest.of(tokenId, serial, updatedMetadata, metadataKey);
    final TokenUpdateNftsResult updateResult =
        protocolLayerClient.executeTokenUpdateNftsTransaction(updateRequest);

    // then
    Assertions.assertNotNull(updateResult);
    Assertions.assertNotNull(updateResult.transactionId());
    Assertions.assertEquals(Status.SUCCESS, updateResult.status());
  }
}
