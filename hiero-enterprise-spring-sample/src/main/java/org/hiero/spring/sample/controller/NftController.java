package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.hiero.base.NftClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.Nft;
import org.hiero.base.data.NftMetadata;
import org.hiero.base.data.Page;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.mirrornode.NftRepository;
import org.hiero.base.mirrornode.TokenRepository;
import org.hiero.spring.sample.dto.nft.NftCreateRequest;
import org.hiero.spring.sample.dto.nft.NftMintRequest;
import org.hiero.spring.sample.dto.nft.NftTransferRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Hiero NFT operations.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1/hiero/nfts")
public class NftController {

  private final NftClient nftClient;
  private final NftRepository nftRepository;
  private final TokenRepository tokenRepository;

  public NftController(
      final NftClient nftClient,
      final NftRepository nftRepository,
      final TokenRepository tokenRepository) {
    this.nftClient = Objects.requireNonNull(nftClient, "nftClient must not be null");
    this.nftRepository = Objects.requireNonNull(nftRepository, "nftRepository must not be null");
    this.tokenRepository = Objects.requireNonNull(tokenRepository, "tokenRepository must not be null");
  }

  /**
   * Creates a new NFT type.
   */
  @PostMapping
  public String createNft(@RequestBody final NftCreateRequest request) {
    try {
      final TokenId tokenId = nftClient.createNftType(request.name(), request.symbol());
      return "NFT Type " + tokenId + " created successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create NFT type", e);
    }
  }

  /**
   * Retrieves detailed information about an NFT type.
   */
  @GetMapping("/{nftId}")
  public TokenInfo getNftById(@PathVariable("nftId") final String nftId) {
    try {
      return tokenRepository.findById(nftId)
          .orElseThrow(() -> new RuntimeException("NFT Type not found: " + nftId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve NFT info for " + nftId, e);
    }
  }

  /**
   * Mints a new NFT instance.
   */
  @PostMapping("/{nftId}/mint")
  public String mintNft(
      @PathVariable("nftId") final String nftId,
      @RequestBody final NftMintRequest request) {
    try {
      final byte[] metadata = request.metadata().getBytes(StandardCharsets.UTF_8);
      final long serialNumber = nftClient.mintNft(nftId, metadata);
      return "Minted NFT instance of " + nftId + " with serial number " + serialNumber;
    } catch (final Exception e) {
      throw new RuntimeException("Failed to mint NFT for " + nftId, e);
    }
  }

  /**
   * Retrieves a specific NFT instance by ID and serial number.
   */
  @GetMapping("/{nftId}/serial/{serial}")
  public Nft getNftBySerial(
      @PathVariable("nftId") final String nftId,
      @PathVariable("serial") final long serial) {
    try {
      return nftRepository.findByTypeAndSerial(nftId, serial)
          .orElseThrow(() -> new RuntimeException("NFT not found: " + nftId + " Serial: " + serial));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve NFT instance", e);
    }
  }

  /**
   * Transfers an NFT instance between accounts.
   */
  @PostMapping("/{nftId}/transfer")
  public String transferNft(
      @PathVariable("nftId") final String nftId,
      @RequestBody final NftTransferRequest request) {
    try {
      final TokenId tokenId = TokenId.fromString(nftId);
      final Account fromAccount = Account.of(
          AccountId.fromString(request.fromAccountId()),
          PrivateKey.fromString(request.fromPrivateKey())
      );
      final AccountId toAccountId = AccountId.fromString(request.toAccountId());

      nftClient.transferNft(tokenId, request.serialNumber(), fromAccount, toAccountId);
      return "Transferred NFT " + nftId + " (Serial: " + request.serialNumber() + ") to " + request.toAccountId();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to transfer NFT", e);
    }
  }
}
