package org.hiero.spring.sample.dto.token;

/**
 * Request to transfer Hiero fungible tokens between accounts.
 *
 * @param fromAccountId The ID of the account to transfer tokens from.
 * @param fromPrivateKey The private key of the sender account (required).
 * @param toAccountId The ID of the account to transfer tokens to.
 * @param amount The amount of tokens to transfer.
 */
public record TokenTransferRequest(
    String fromAccountId,
    String fromPrivateKey,
    String toAccountId,
    long amount
) {}
