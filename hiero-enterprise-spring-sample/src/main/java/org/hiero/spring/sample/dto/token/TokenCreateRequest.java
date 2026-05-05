package org.hiero.spring.sample.dto.token;

/**
 * Request to create a new Hiero fungible token.
 *
 * @param name The name of the token.
 * @param symbol The symbol of the token.
 * @param decimals The number of decimals for the token (optional, defaults to 0).
 * @param initialSupply The initial supply of the token (optional, defaults to 0).
 */
public record TokenCreateRequest(
    String name,
    String symbol,
    Integer decimals,
    Long initialSupply
) {}
