package org.hiero.spring.sample.dto.token;

/**
 * Request to mint more of a Hiero fungible token.
 *
 * @param amount The amount to mint.
 * @param supplyKey The supply key of the token (required if the token has a supply key).
 */
public record TokenMintRequest(
    long amount,
    String supplyKey
) {}
