package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.Objects;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.spring.sample.dto.account.AccountCreateRequest;
import org.hiero.spring.sample.dto.account.AccountDeleteRequest;
import org.hiero.spring.sample.dto.account.AccountResponse;
import org.hiero.spring.sample.dto.account.AccountUpdateRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Hiero account operations.
 * This controller provides endpoints for account lifecycle management and queries.
 */
@Tag(name = "Accounts", description = "Operations related to Hiero account lifecycle and balance queries")
@RestController
@RequestMapping("/api/v1/hiero/accounts")
public class AccountController {

  private final AccountClient accountClient;
  private final AccountRepository accountRepository;

  public AccountController(
      final AccountClient accountClient,
      final AccountRepository accountRepository) {
    this.accountClient = Objects.requireNonNull(accountClient, "accountClient must not be null");
    this.accountRepository =
        Objects.requireNonNull(accountRepository, "accountRepository must not be null");
  }

  /**
   * Creates a new Hiero account.
   *
   * @param request The account creation request containing optional initial balance.
   * @return Success message with the new account ID.
   */
  @Operation(summary = "Create a new Hiero account", description = "Creates a new Hiero account with an optional initial balance.")
  @PostMapping
  public AccountResponse createAccount(@RequestBody(required = false) final AccountCreateRequest request) {
    try {
      final Hbar initialBalance = (request != null && request.initialBalance() != null)
          ? Hbar.from(request.initialBalance())
          : Hbar.ZERO;
      
      final Account account = accountClient.createAccount(initialBalance);
      return new AccountResponse(
          account.accountId().toString(),
          account.publicKey().toString(),
          account.privateKey().toString()
      );
    } catch (final Exception e) {
      throw new RuntimeException("Failed to create account", e);
    }
  }

  /**
   * Updates an existing Hiero account.
   *
   * @param request The account update request containing new key or memo.
   * @return Success message.
   */
  @Operation(summary = "Update an existing Hiero account", description = "Updates an account's metadata such as keys or memo.")
  @PutMapping
  public String updateAccount(@RequestBody final AccountUpdateRequest request) {
    try {
      if (request.accountId() == null || request.privateKey() == null) {
        throw new IllegalArgumentException("Missing required fields: accountId and privateKey are mandatory.");
      }
      final AccountId accountId = AccountId.fromString(request.accountId().trim());
      final PrivateKey currentKey = PrivateKey.fromString(request.privateKey().trim());
      final Account account = Account.of(accountId, currentKey);

      if (request.newPrivateKey() != null && request.memo() != null) {
        accountClient.updateAccount(account, PrivateKey.fromString(request.newPrivateKey().trim()), request.memo());
      } else if (request.newPrivateKey() != null) {
        accountClient.updateAccountKey(account, PrivateKey.fromString(request.newPrivateKey().trim()));
      } else if (request.memo() != null) {
        accountClient.updateAccountMemo(account, request.memo());
      }

      return "Account " + request.accountId() + " updated successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to update account", e);
    }
  }

  /**
   * Deletes a Hiero account.
   *
   * @param request The account deletion request.
   */
  @Operation(summary = "Delete a Hiero account", description = "Deletes an account and optionally transfers remaining balance to another account.")
  @DeleteMapping
  public String deleteAccount(@RequestBody final AccountDeleteRequest request) {
    try {
      if (request.accountId() == null || request.privateKey() == null) {
        throw new IllegalArgumentException("Missing required fields: accountId and privateKey are mandatory.");
      }
      final AccountId accountId = AccountId.fromString(request.accountId().trim());
      final PrivateKey privateKey = PrivateKey.fromString(request.privateKey().trim());
      final Account account = Account.of(accountId, privateKey);

      if (request.transferToAccountId() != null) {
        final AccountId transferTo = AccountId.fromString(request.transferToAccountId().trim());
        final Account transferTarget = Account.of(transferTo, PrivateKey.generateED25519());
        accountClient.deleteAccount(account, transferTarget);
      } else {
        accountClient.deleteAccount(account);
      }

      return "Account " + request.accountId() + " deleted successfully!";
    } catch (final Exception e) {
      throw new RuntimeException("Failed to delete account", e);
    }
  }

  /**
   * Retrieves the balance of a Hiero account.
   *
   * @param accountId The ID of the account to query.
   * @return The balance in Hbar.
   */
  @Operation(summary = "Get account balance", description = "Retrieves the current balance of a Hiero account in Hbar.")
  @GetMapping("/balance/{accountId}")
  public String getBalance(@PathVariable("accountId") final String accountId) {
    try {
      final Hbar balance = accountClient.getAccountBalance(accountId.trim());
      return balance.toString();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve balance for account " + accountId, e);
    }
  }

  /**
   * Retrieves detailed information about a Hiero account from the mirror node.
   *
   * @param accountId The ID of the account to query.
   * @return The AccountInfo object.
   */
  @Operation(summary = "Get account information", description = "Retrieves detailed account information from the Hiero mirror node.")
  @GetMapping("/info/{accountId}")
  public AccountInfo getInfo(@PathVariable("accountId") final String accountId) {
    try {
      final String trimmedAccountId = accountId.trim();
      return accountRepository.findById(trimmedAccountId)
          .orElseThrow(() -> new RuntimeException("Account not found: " + trimmedAccountId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve info for account " + accountId, e);
    }
  }
}
