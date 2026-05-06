package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.hiero.spring.sample.dto.nft.NftAssociateRequest;
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
@Tag(name = "Non-Fungible Tokens", description = "Operations related to Hiero Non-Fungible Token Service (NFTs)")
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
  @Operation(summary = "Create an NFT type", description = "Creates a new HTS NFT collection.")
  @PostMapping
  public String createNft(@RequestBody final NftCreateRequest request) {
    try {
      if (request.name() == null || request.symbol() == null) {
        throw new IllegalArgumentException("Missing required fields: name and symbol are mandatory.");
      }
      final TokenId tokenId = nftClient.createNftType(request.name(), request.symbol());
      return "NFT Type " + tokenId + " created successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create NFT type", e);
    }
  }

  /**
   * Retrieves detailed information about an NFT type.
   */
  @Operation(summary = "Get NFT type information", description = "Retrieves detailed information about an NFT collection from the mirror node.")
  @GetMapping("/{nftId}")
  public TokenInfo getNftById(@PathVariable("nftId") final String nftId) {
    try {
      final String trimmedNftId = nftId.trim();
      return tokenRepository.findById(trimmedNftId)
          .orElseThrow(() -> new RuntimeException("NFT Type not found: " + trimmedNftId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve NFT info for " + nftId, e);
    }
  }

  /**
   * Mints a new NFT instance.
   */
  @Operation(summary = "Mint an NFT", description = "Mints a new serial number for an existing NFT collection.")
  @PostMapping("/{nftId}/mint")
  public String mintNft(
      @PathVariable("nftId") final String nftId,
      @RequestBody final NftMintRequest request) {
    try {
      if (request.metadata() == null) {
        throw new IllegalArgumentException("Missing required field: metadata is mandatory.");
      }
      final String trimmedNftId = nftId.trim();
      final byte[] metadata = request.metadata().getBytes(StandardCharsets.UTF_8);
      final long serialNumber = nftClient.mintNft(trimmedNftId, metadata);
      return "Minted NFT instance of " + nftId + " with serial number " + serialNumber;
    } catch (final Exception e) {
      throw new RuntimeException("Failed to mint NFT for " + nftId, e);
    }
  }

  /**
   * Retrieves a specific NFT instance by ID and serial number.
   */
  @Operation(summary = "Get NFT instance", description = "Retrieves information about a specific NFT instance (serial number) from the mirror node.")
  @GetMapping("/{nftId}/serial/{serial}")
  public Nft getNftBySerial(
      @PathVariable("nftId") final String nftId,
      @PathVariable("serial") final long serial) {
    try {
      final String trimmedNftId = nftId.trim();
      return nftRepository.findByTypeAndSerial(trimmedNftId, serial)
          .orElseThrow(() -> new RuntimeException("NFT not found: " + trimmedNftId + " Serial: " + serial));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve NFT instance", e);
    }
  }

  /**
   * Transfers an NFT instance between accounts.
   */
  @Operation(summary = "Transfer an NFT", description = "Transfers a specific NFT serial number between two Hiero accounts.")
  @PostMapping("/{nftId}/transfer")
  public String transferNft(
      @PathVariable("nftId") final String nftId,
      @RequestBody final NftTransferRequest request) {
    try {
      if (request.fromAccountId() == null || request.fromPrivateKey() == null || request.toAccountId() == null) {
        throw new IllegalArgumentException("Missing required fields: fromAccountId, fromPrivateKey, and toAccountId are mandatory.");
      }
      final TokenId tokenId = TokenId.fromString(nftId.trim());
      final Account fromAccount = Account.of(
          AccountId.fromString(request.fromAccountId().trim()),
          PrivateKey.fromString(request.fromPrivateKey().trim())
      );
      final AccountId toAccountId = AccountId.fromString(request.toAccountId().trim());

      nftClient.transferNft(tokenId, request.serialNumber(), fromAccount, toAccountId);
      return "Transferred NFT " + nftId + " (Serial: " + request.serialNumber() + ") to " + request.toAccountId();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to transfer NFT", e);
    }
  }

  /**
   * Associates an account with a Hiero NFT collection.
   */
  @Operation(summary = "Associate account with NFT", description = "Explicitly associates a Hiero account with an NFT collection. Required before receiving NFTs.")
  @PostMapping("/{nftId}/associate")
  public String associateNft(
      @PathVariable("nftId") final String nftId,
      @RequestBody final NftAssociateRequest request) {
    try {
      if (request.accountId() == null || request.privateKey() == null) {
        throw new IllegalArgumentException("Missing required fields: accountId and privateKey are mandatory.");
      }
      final TokenId id = TokenId.fromString(nftId.trim());
      final Account account = Account.of(
          AccountId.fromString(request.accountId().trim()),
          PrivateKey.fromString(request.privateKey().trim())
      );

      nftClient.associateNft(id, account);
      return "Account " + request.accountId() + " associated with NFT collection " + nftId + " successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to associate account " + request.accountId() + " with NFT " + nftId, e);
    }
  }
}
