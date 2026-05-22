package org.hiero.spring.sample.dto.nft;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request DTO for creating a new NFT type. */
@Schema(
    name = "Non-Fungible Token: Create Request",
    description = "Request DTO for creating a new Hiero NFT collection.")
public record NftCreateRequest(
    @Schema(description = "The name of the NFT collection.", example = "Hiero Heroes") String name,
    @Schema(description = "The symbol of the NFT collection.", example = "HERO") String symbol) {}
