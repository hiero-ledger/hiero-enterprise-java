package org.hiero.spring.sample.web;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Map;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.HieroContext;
import org.hiero.base.HieroException;
import org.hiero.base.NftClient;
import org.hiero.base.data.FungibleTokenRequest;
import org.hiero.base.data.NftCollectionRequest;
import org.hiero.base.data.TokenActionRequest;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.base.mirrornode.TokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST Controller for advanced Token Management operations. */
@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {

  private final FungibleTokenClient fungibleClient;
  private final NftClient nftClient;
  private final HieroContext hieroContext;
  private final TokenRepository tokenRepository;
  private final NftRepository nftRepository;

  public TokenController(
      final FungibleTokenClient fungibleClient,
      final NftClient nftClient,
      final HieroContext hieroContext,
      final TokenRepository tokenRepository,
      final NftRepository nftRepository) {
    this.fungibleClient = Objects.requireNonNull(fungibleClient, "fungibleClient must not be null");
    this.nftClient = Objects.requireNonNull(nftClient, "nftClient must not be null");
    this.hieroContext = Objects.requireNonNull(hieroContext, "hieroContext must not be null");
    this.tokenRepository =
        Objects.requireNonNull(tokenRepository, "tokenRepository must not be null");
    this.nftRepository = Objects.requireNonNull(nftRepository, "nftRepository must not be null");
  }

  @GetMapping("/{tokenId}")
  public ResponseEntity<?> getInfo(@PathVariable("tokenId") final String tokenId)
      throws HieroException {
    return tokenRepository
        .findById(tokenId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{tokenId}/balances")
  public ResponseEntity<?> getBalances(@PathVariable("tokenId") final String tokenId)
      throws HieroException {
    return ResponseEntity.ok(tokenRepository.getBalances(tokenId));
  }
  
  @GetMapping("/{tokenId}/nfts/{serial}")
  public ResponseEntity<?> getNftInfo(
      @PathVariable("tokenId") final String tokenId, @PathVariable("serial") final long serial)
      throws HieroException {
    return nftRepository
        .findByTypeAndSerial(tokenId, serial)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/fungible")
  public ResponseEntity<?> createFungible(@RequestBody FungibleTokenRequest request)
      throws HieroException {
    final TokenId tokenId = fungibleClient.createToken(request.name(), request.symbol());
    return ResponseEntity.ok(Map.of("tokenId", tokenId.toString(), "status", "CREATED"));
  }

  @PostMapping("/nft")
  public ResponseEntity<?> createNft(@RequestBody NftCollectionRequest request)
      throws HieroException {
    final TokenId tokenId = nftClient.createNftType(request.name(), request.symbol());
    return ResponseEntity.ok(Map.of("tokenId", tokenId.toString(), "status", "CREATED"));
  }

  @PostMapping("/{tokenId}/mint")
  public ResponseEntity<?> mint(
      @PathVariable("tokenId") final String tokenId, @RequestBody final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.metadata() != null) {
      final long serial = nftClient.mintNft(id, request.metadata().getBytes());
      return ResponseEntity.ok(Map.of("tokenId", tokenId, "serial", serial));
    } else {
      final long supply = fungibleClient.mintToken(id, request.amount());
      return ResponseEntity.ok(Map.of("tokenId", tokenId, "newTotalSupply", supply));
    }
  }

  @PostMapping("/{tokenId}/burn")
  public ResponseEntity<?> burn(
      @PathVariable("tokenId") final String tokenId, @RequestBody final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    final long supply = fungibleClient.burnToken(id, request.amount());
    return ResponseEntity.ok(Map.of("tokenId", tokenId, "newTotalSupply", supply));
  }

  @PostMapping("/{tokenId}/associate")
  public ResponseEntity<?> associate(
      @PathVariable("tokenId") final String tokenId, @RequestBody final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.accountKey() != null) {
      fungibleClient.associateToken(
          id,
          AccountId.fromString(request.toAccountId()),
          PrivateKey.fromString(request.accountKey()));
    } else {
      // Use HieroContext operator if no key provided
      fungibleClient.associateToken(id, hieroContext.getOperatorAccount());
    }
    return ResponseEntity.ok(Map.of("tokenId", tokenId, "status", "ASSOCIATED"));
  }

  @PostMapping("/{tokenId}/dissociate")
  public ResponseEntity<?> dissociate(
      @PathVariable("tokenId") final String tokenId, @RequestBody final TokenActionRequest request)
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
    return ResponseEntity.ok(Map.of("tokenId", tokenId, "status", "DISSOCIATED"));
  }

  @PostMapping("/{tokenId}/transfer")
  public ResponseEntity<?> transfer(
      @PathVariable("tokenId") final String tokenId, @RequestBody final TokenActionRequest request)
      throws HieroException {
    final TokenId id = TokenId.fromString(tokenId);
    if (request.metadata() != null) {
      // NFT Transfer
      nftClient.transferNft(
          id,
          Long.parseLong(request.metadata()),
          hieroContext.getOperatorAccount(),
          AccountId.fromString(request.toAccountId()));
    } else {
      // Fungible Transfer
      fungibleClient.transferToken(id, request.toAccountId(), request.amount());
    }
    return ResponseEntity.ok(
        Map.of("tokenId", tokenId, "to", request.toAccountId(), "status", "TRANSFERRED"));
  }

  @PostMapping("/{tokenId}/pause")
  public ResponseEntity<?> pause(@PathVariable("tokenId") final String tokenId)
      throws HieroException {
    nftClient.pauseNft(TokenId.fromString(tokenId));
    return ResponseEntity.ok(Map.of("tokenId", tokenId, "status", "PAUSED"));
  }

  @PostMapping("/{tokenId}/unpause")
  public ResponseEntity<?> unpause(@PathVariable("tokenId") final String tokenId)
      throws HieroException {
    nftClient.unpauseNft(TokenId.fromString(tokenId));
    return ResponseEntity.ok(Map.of("tokenId", tokenId, "status", "ACTIVE"));
  }

  @PostMapping("/{tokenId}/freeze/{accountId}")
  public ResponseEntity<?> freeze(
      @PathVariable("tokenId") final String tokenId,
      @PathVariable("accountId") final String accountId)
      throws HieroException {
    fungibleClient.freezeAccount(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return ResponseEntity.ok(
        Map.of("tokenId", tokenId, "accountId", accountId, "status", "FROZEN"));
  }

  @PostMapping("/{tokenId}/unfreeze/{accountId}")
  public ResponseEntity<?> unfreeze(
      @PathVariable("tokenId") final String tokenId,
      @PathVariable("accountId") final String accountId)
      throws HieroException {
    fungibleClient.unfreezeAccount(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return ResponseEntity.ok(
        Map.of("tokenId", tokenId, "accountId", accountId, "status", "ACTIVE"));
  }

  @PostMapping("/{tokenId}/kyc/{accountId}")
  public ResponseEntity<?> grantKyc(
      @PathVariable("tokenId") final String tokenId,
      @PathVariable("accountId") final String accountId)
      throws HieroException {
    nftClient.grantKyc(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return ResponseEntity.ok(
        Map.of("tokenId", tokenId, "accountId", accountId, "status", "KYC_GRANTED"));
  }

  @DeleteMapping("/{tokenId}/kyc/{accountId}")
  public ResponseEntity<?> revokeKyc(
      @PathVariable("tokenId") final String tokenId,
      @PathVariable("accountId") final String accountId)
      throws HieroException {
    nftClient.revokeKyc(TokenId.fromString(tokenId), AccountId.fromString(accountId));
    return ResponseEntity.ok(
        Map.of("tokenId", tokenId, "accountId", accountId, "status", "KYC_REVOKED"));
  }

  @DeleteMapping("/{tokenId}")
  public ResponseEntity<?> delete(@PathVariable("tokenId") final String tokenId)
      throws HieroException {
    fungibleClient.deleteToken(TokenId.fromString(tokenId));
    return ResponseEntity.ok(Map.of("tokenId", tokenId, "status", "DELETED"));
  }
}
