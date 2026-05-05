package org.hiero.spring.sample.dto.account;

/**
 * Request to update an existing Hiero account.
 *
 * @param accountId The ID of the account to update.
 * @param privateKey The current private key of the account.
 * @param newPrivateKey The new private key to set (optional).
 * @param memo The new memo to set (optional).
 */
public record AccountUpdateRequest(
    String accountId,
    String privateKey,
    String newPrivateKey,
    String memo
) {}
