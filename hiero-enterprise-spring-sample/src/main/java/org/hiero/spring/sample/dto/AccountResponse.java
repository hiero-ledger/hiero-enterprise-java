package org.hiero.spring.sample.dto;

/**
 * Response containing Hiero account details.
 *
 * @param accountId The ID of the account.
 * @param publicKey The public key of the account.
 * @param privateKey The private key of the account.
 */
public record AccountResponse(
    String accountId,
    String publicKey,
    String privateKey
) {}
