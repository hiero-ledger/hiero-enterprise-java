package org.hiero.spring.sample.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request to mint more of a Hiero fungible token. */
@Schema(
    name = "Fungible Token: Mint Request",
    description = "Request DTO for minting additional units of a fungible token.")
public record TokenMintRequest(
    @Schema(description = "The amount of tokens to mint.", example = "500000") long amount,
    @Schema(
            description = "The supply key of the token (required if the token has a supply key).",
            example = "302e020100300506032b657004220420...")
        String supplyKey) {}
