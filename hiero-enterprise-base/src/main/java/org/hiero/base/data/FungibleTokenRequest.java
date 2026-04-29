package org.hiero.base.data;

/** Data transfer object for creating a fungible token. */
public record FungibleTokenRequest(
    String name, String symbol, Integer decimals, Long initialSupply) {}
