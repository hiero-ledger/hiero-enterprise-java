package org.hiero.spring.sample.dto.account;

/**
 * Request to delete a Hiero account.
 *
 * @param accountId The ID of the account to delete.
 * @param privateKey The private key of the account to delete.
 * @param transferToAccountId The ID of the account to transfer remaining funds to (optional, defaults to operator).
 */
public record AccountDeleteRequest(
    String accountId,
    String privateKey,
    String transferToAccountId
) {}
