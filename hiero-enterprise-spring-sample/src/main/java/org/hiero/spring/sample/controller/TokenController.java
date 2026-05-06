package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.mirrornode.TokenRepository;
import org.hiero.spring.sample.dto.token.TokenAssociateRequest;
import org.hiero.spring.sample.dto.token.TokenCreateRequest;
import org.hiero.spring.sample.dto.token.TokenMintRequest;
import org.hiero.spring.sample.dto.token.TokenTransferRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Fungible Tokens", description = "Operations related to Hiero fungible tokens (HTS)")
@RestController
@CrossOrigin
@RequestMapping("/api/v1/hiero/fungible-token")
public class TokenController {

  private final FungibleTokenClient tokenClient;
  private final TokenRepository tokenRepository;

  public TokenController(
      final FungibleTokenClient tokenClient,
      final TokenRepository tokenRepository) {
    this.tokenClient = Objects.requireNonNull(tokenClient, "tokenClient must not be null");
    this.tokenRepository = Objects.requireNonNull(tokenRepository, "tokenRepository must not be null");
  }

  /**
   * Creates a new Hiero fungible token.
   */
  @Operation(summary = "Create a new fungible token", description = "Creates a new Hiero fungible token (HTS).")
  @PostMapping
  public String createToken(@RequestBody final TokenCreateRequest request) {
    try {
      if (request.name() == null || request.symbol() == null) {
        throw new IllegalArgumentException("Missing required fields: name and symbol are mandatory.");
      }
      final TokenId tokenId = tokenClient.createToken(request.name(), request.symbol());
      return "Token " + tokenId + " created successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create token", e);
    }
  }

  /**
   * Retrieves detailed information about a Hiero fungible token.
   */
  @Operation(summary = "Get token information", description = "Retrieves detailed information about a fungible token from the mirror node.")
  @GetMapping("/{tokenId}")
  public TokenInfo getToken(@PathVariable("tokenId") final String tokenId) {
    try {
      final String trimmedTokenId = tokenId.trim();
      return tokenRepository.findById(trimmedTokenId)
          .orElseThrow(() -> new RuntimeException("Token not found: " + trimmedTokenId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve token info for " + tokenId, e);
    }
  }

  /**
   * Mints more of a Hiero fungible token.
   */
  @Operation(summary = "Mint tokens", description = "Mints additional units of a fungible token to the treasury account.")
  @PostMapping("/{tokenId}/mint")
  public String mintToken(
      @PathVariable("tokenId") final String tokenId,
      @RequestBody final TokenMintRequest request) {
    try {
      final String trimmedTokenId = tokenId.trim();
      final long newTotalSupply;
      if (request.supplyKey() != null) {
        newTotalSupply = tokenClient.mintToken(trimmedTokenId, request.supplyKey(), request.amount());
      } else {
        newTotalSupply = tokenClient.mintToken(trimmedTokenId, request.amount());
      }
      return "Minted " + request.amount() + " units. New total supply: " + newTotalSupply;
    } catch (final Exception e) {
      throw new RuntimeException("Failed to mint token " + tokenId, e);
    }
  }

  /**
   * Transfers Hiero fungible tokens between accounts.
   */
  @Operation(summary = "Transfer tokens", description = "Transfers fungible tokens between two Hiero accounts.")
  @PostMapping("/{tokenId}/transfer")
  public String transferToken(
      @PathVariable("tokenId") final String tokenId,
      @RequestBody final TokenTransferRequest request) {
    try {
      if (request.fromAccountId() == null || request.fromPrivateKey() == null || request.toAccountId() == null) {
        throw new IllegalArgumentException("Missing required fields: fromAccountId, fromPrivateKey, and toAccountId are mandatory.");
      }
      final TokenId id = TokenId.fromString(tokenId.trim());
      final Account fromAccount = Account.of(
          AccountId.fromString(request.fromAccountId().trim()),
          PrivateKey.fromString(request.fromPrivateKey().trim())
      );
      final AccountId toAccount = AccountId.fromString(request.toAccountId().trim());

      tokenClient.transferToken(id, fromAccount, toAccount, request.amount());
      return "Transferred " + request.amount() + " tokens from " + request.fromAccountId() + " to " + request.toAccountId();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to transfer token " + tokenId, e);
    }
  }

  /**
   * Associates an account with a Hiero fungible token.
   */
  @Operation(summary = "Associate account with token", description = "Explicitly associates a Hiero account with a fungible token. Required before receiving tokens.")
  @PostMapping("/{tokenId}/associate")
  public String associateToken(
      @PathVariable("tokenId") final String tokenId,
      @RequestBody final TokenAssociateRequest request) {
    try {
      if (request.accountId() == null || request.privateKey() == null) {
        throw new IllegalArgumentException("Missing required fields: accountId and privateKey are mandatory.");
      }
      final TokenId id = TokenId.fromString(tokenId.trim());
      final Account account = Account.of(
          AccountId.fromString(request.accountId().trim()),
          PrivateKey.fromString(request.privateKey().trim())
      );

      tokenClient.associateToken(id, account);
      return "Account " + request.accountId() + " associated with token " + tokenId + " successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to associate account " + request.accountId() + " with token " + tokenId, e);
    }
  }
}
