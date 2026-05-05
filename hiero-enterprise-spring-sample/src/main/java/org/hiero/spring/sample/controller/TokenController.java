package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.hiero.base.FungibleTokenClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.mirrornode.TokenRepository;
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
  @PostMapping
  public String createToken(@RequestBody final TokenCreateRequest request) {
    try {
      final TokenId tokenId = tokenClient.createToken(request.name(), request.symbol());
      return "Token " + tokenId + " created successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create token", e);
    }
  }

  /**
   * Retrieves detailed information about a Hiero fungible token.
   */
  @GetMapping("/{tokenId}")
  public TokenInfo getToken(@PathVariable("tokenId") final String tokenId) {
    try {
      return tokenRepository.findById(tokenId)
          .orElseThrow(() -> new RuntimeException("Token not found: " + tokenId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve token info for " + tokenId, e);
    }
  }

  /**
   * Mints more of a Hiero fungible token.
   */
  @PostMapping("/{tokenId}/mint")
  public String mintToken(
      @PathVariable("tokenId") final String tokenId,
      @RequestBody final TokenMintRequest request) {
    try {
      final long newTotalSupply;
      if (request.supplyKey() != null) {
        newTotalSupply = tokenClient.mintToken(tokenId, request.supplyKey(), request.amount());
      } else {
        newTotalSupply = tokenClient.mintToken(tokenId, request.amount());
      }
      return "Minted " + request.amount() + " units. New total supply: " + newTotalSupply;
    } catch (final Exception e) {
      throw new RuntimeException("Failed to mint token " + tokenId, e);
    }
  }

  /**
   * Transfers Hiero fungible tokens between accounts.
   */
  @PostMapping("/{tokenId}/transfer")
  public String transferToken(
      @PathVariable("tokenId") final String tokenId,
      @RequestBody final TokenTransferRequest request) {
    try {
      final TokenId id = TokenId.fromString(tokenId);
      final Account fromAccount = Account.of(
          AccountId.fromString(request.fromAccountId()),
          PrivateKey.fromString(request.fromPrivateKey())
      );
      final AccountId toAccount = AccountId.fromString(request.toAccountId());

      tokenClient.transferToken(id, fromAccount, toAccount, request.amount());
      return "Transferred " + request.amount() + " tokens from " + request.fromAccountId() + " to " + request.toAccountId();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to transfer token " + tokenId, e);
    }
  }
}
