package org.hiero.microprofile.sample.resource;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroContext;
import org.hiero.base.HieroException;
import org.hiero.base.NftClient;
import org.hiero.base.data.FungibleTokenRequest;
import org.hiero.base.data.NftCollectionRequest;
import org.hiero.base.data.TokenActionRequest;
import org.hiero.base.mirrornode.TokenRepository;

/** JAX-RS Resource for advanced Token Management operations. */
@Path("/api/v1/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokenResource {

  private final FungibleTokenClient fungibleClient;
  private final NftClient nftClient;
  private final HieroContext hieroContext;
  private final TokenRepository tokenRepository;

  @Inject
  public TokenResource(
      final FungibleTokenClient fungibleClient,
      final NftClient nftClient,
      final HieroContext hieroContext,
      final TokenRepository tokenRepository) {
    this.fungibleClient = Objects.requireNonNull(fungibleClient, "fungibleClient must not be null");
    this.nftClient = Objects.requireNonNull(nftClient, "nftClient must not be null");
    this.hieroContext = Objects.requireNonNull(hieroContext, "hieroContext must not be null");
    this.tokenRepository =
        Objects.requireNonNull(tokenRepository, "tokenRepository must not be null");
  }

  @GET
  @Path("/{tokenId}")
  public Response getInfo(@PathParam("tokenId") final String tokenId) throws HieroException {
    return tokenRepository
        .findById(tokenId)
        .map(info -> Response.ok(info).build())
        .orElse(Response.status(Response.Status.NOT_FOUND).build());
  }

  @GET
  @Path("/{tokenId}/balances")
  public Response getBalances(@PathParam("tokenId") final String tokenId) throws HieroException {
    return Response.ok(tokenRepository.getBalances(tokenId)).build();
  }

  @POST
  @Path("/fungible")
  public Response createFungible(final FungibleTokenRequest request) throws HieroException {
    final TokenId tokenId = fungibleClient.createToken(request.name(), request.symbol());
    return Response.ok(Map.of("tokenId", tokenId.toString(), "status", "CREATED")).build();
  }

  @POST
  @Path("/nft")
  public Response createNft(final NftCollectionRequest request) throws HieroException {
    final TokenId tokenId = nftClient.createNftType(request.name(), request.symbol());
    return Response.ok(Map.of("tokenId", tokenId.toString(), "status", "CREATED")).build();
  }

  @POST
  @Path("/{tokenId}/mint")
  public Response mint(@PathParam("tokenId") final String tokenId, final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.metadata() != null) {
      final long serial = nftClient.mintNft(id, request.metadata().getBytes());
      return Response.ok(Map.of("tokenId", tokenId, "serial", serial)).build();
    } else {
      final long supply = fungibleClient.mintToken(id, request.amount());
      return Response.ok(Map.of("tokenId", tokenId, "newTotalSupply", supply)).build();
    }
  }

  @POST
  @Path("/{tokenId}/burn")
  public Response burn(@PathParam("tokenId") final String tokenId, final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    final long supply = fungibleClient.burnToken(id, request.amount());
    return Response.ok(Map.of("tokenId", tokenId, "newTotalSupply", supply)).build();
  }

  @POST
  @Path("/{tokenId}/associate")
  public Response associate(
      @PathParam("tokenId") final String tokenId, final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.accountKey() != null) {
      fungibleClient.associateToken(
          id,
          AccountId.fromString(request.toAccountId()),
          PrivateKey.fromString(request.accountKey()));
    } else {
      fungibleClient.associateToken(id, hieroContext.getOperatorAccount());
    }
    return Response.ok(Map.of("tokenId", tokenId, "status", "ASSOCIATED")).build();
  }

  @POST
  @Path("/{tokenId}/dissociate")
  public Response dissociate(
      @PathParam("tokenId") final String tokenId, final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.accountKey() != null) {
      fungibleClient.dissociateToken(
          id,
          AccountId.fromString(request.toAccountId()),
          PrivateKey.fromString(request.accountKey()));
    } else {
      fungibleClient.dissociateToken(id, hieroContext.getOperatorAccount());
    }
    return Response.ok(Map.of("tokenId", tokenId, "status", "DISSOCIATED")).build();
  }

  @POST
  @Path("/{tokenId}/transfer")
  public Response transfer(
      @PathParam("tokenId") final String tokenId, final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.metadata() != null) {
      // NFT Transfer (using metadata as serial)
      nftClient.transferNft(
          id,
          Long.parseLong(request.metadata()),
          hieroContext.getOperatorAccount(),
          AccountId.fromString(request.toAccountId()));
    } else {
      // Fungible Transfer
      fungibleClient.transferToken(id, request.toAccountId(), request.amount());
    }
    return Response.ok(
            Map.of("tokenId", tokenId, "to", request.toAccountId(), "status", "TRANSFERRED"))
        .build();
  }

  @POST
  @Path("/{tokenId}/pause")
  public Response pause(@PathParam("tokenId") final String tokenId) throws HieroException {
    nftClient.pauseNft(TokenId.fromString(tokenId));
    return Response.ok(Map.of("tokenId", tokenId, "status", "PAUSED")).build();
  }

  @POST
  @Path("/{tokenId}/unpause")
  public Response unpause(@PathParam("tokenId") final String tokenId) throws HieroException {
    nftClient.unpauseNft(TokenId.fromString(tokenId));
    return Response.ok(Map.of("tokenId", tokenId, "status", "ACTIVE")).build();
  }

  @POST
  @Path("/{tokenId}/freeze/{accountId}")
  public Response freeze(
      @PathParam("tokenId") final String tokenId, @PathParam("accountId") final String accountId)
      throws HieroException {
    fungibleClient.freezeAccount(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return Response.ok(Map.of("tokenId", tokenId, "accountId", accountId, "status", "FROZEN"))
        .build();
  }

  @POST
  @Path("/{tokenId}/unfreeze/{accountId}")
  public Response unfreeze(
      @PathParam("tokenId") final String tokenId, @PathParam("accountId") final String accountId)
      throws HieroException {
    fungibleClient.unfreezeAccount(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return Response.ok(Map.of("tokenId", tokenId, "accountId", accountId, "status", "ACTIVE"))
        .build();
  }

  @POST
  @Path("/{tokenId}/kyc/{accountId}")
  public Response grantKyc(
      @PathParam("tokenId") final String tokenId, @PathParam("accountId") final String accountId)
      throws HieroException {
    nftClient.grantKyc(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return Response.ok(Map.of("tokenId", tokenId, "accountId", accountId, "status", "KYC_GRANTED"))
        .build();
  }

  @DELETE
  @Path("/{tokenId}/kyc/{accountId}")
  public Response revokeKyc(
      @PathParam("tokenId") final String tokenId, @PathParam("accountId") final String accountId)
      throws HieroException {
    nftClient.revokeKyc(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return Response.ok(Map.of("tokenId", tokenId, "accountId", accountId, "status", "KYC_REVOKED"))
        .build();
  }

  @DELETE
  @Path("/{tokenId}")
  public Response delete(@PathParam("tokenId") final String tokenId) throws HieroException {
    fungibleClient.deleteToken(TokenId.fromString(tokenId));
    return Response.ok(Map.of("tokenId", tokenId, "status", "DELETED")).build();
  }
}
