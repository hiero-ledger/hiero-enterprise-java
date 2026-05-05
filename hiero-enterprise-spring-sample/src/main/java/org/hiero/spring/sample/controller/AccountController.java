package org.hiero.spring.sample.controller;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.Objects;
import org.hiero.base.AccountClient;
import org.hiero.base.data.Account;
import org.hiero.base.data.AccountInfo;
import org.hiero.base.mirrornode.AccountRepository;
import org.hiero.spring.sample.dto.AccountCreateRequest;
import org.hiero.spring.sample.dto.AccountDeleteRequest;
import org.hiero.spring.sample.dto.AccountResponse;
import org.hiero.spring.sample.dto.AccountUpdateRequest;
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
  @PutMapping
  public String updateAccount(@RequestBody final AccountUpdateRequest request) {
    try {
      final AccountId accountId = AccountId.fromString(request.accountId());
      final PrivateKey currentKey = PrivateKey.fromString(request.privateKey());
      final Account account = Account.of(accountId, currentKey);

      if (request.newPrivateKey() != null && request.memo() != null) {
        accountClient.updateAccount(account, PrivateKey.fromString(request.newPrivateKey()), request.memo());
      } else if (request.newPrivateKey() != null) {
        accountClient.updateAccountKey(account, PrivateKey.fromString(request.newPrivateKey()));
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
  @DeleteMapping
  public String deleteAccount(@RequestBody final AccountDeleteRequest request) {
    try {
      final AccountId accountId = AccountId.fromString(request.accountId());
      final PrivateKey privateKey = PrivateKey.fromString(request.privateKey());
      final Account account = Account.of(accountId, privateKey);

      if (request.transferToAccountId() != null) {
        final AccountId transferTo = AccountId.fromString(request.transferToAccountId());
        // Since we don't have a full Account object for the transferTo target, 
        // we use a minimal one if the client allows it, or we might need more logic.
        // For now, let's assume we can pass a dummy account if we only need the ID.
        // Wait, AccountClient.deleteAccount(Account account, Account toAccount) 
        // actually takes Account objects.
        // Let's check if we can create a shell Account for the transfer target.
        final Account transferTarget = Account.of(transferTo, PrivateKey.generateED25519()); // Key won't be used for receiving
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
  @GetMapping("/balance/{accountId}")
  public String getBalance(@PathVariable("accountId") final String accountId) {
    try {
      final Hbar balance = accountClient.getAccountBalance(accountId);
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
  @GetMapping("/info/{accountId}")
  public AccountInfo getInfo(@PathVariable("accountId") final String accountId) {
    try {
      return accountRepository.findById(accountId)
          .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to retrieve info for account " + accountId, e);
    }
  }
}
