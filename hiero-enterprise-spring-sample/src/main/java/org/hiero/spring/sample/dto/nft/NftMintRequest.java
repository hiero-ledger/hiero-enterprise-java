package org.hiero.spring.sample.dto.nft;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request DTO for minting a new NFT instance. */
@Schema(
    name = "Non-Fungible Token: Mint Request",
    description = "Request DTO for minting a new NFT instance into a collection.")
public record NftMintRequest(
    @Schema(
            description = "The metadata for the NFT instance (e.g., IPFS CID).",
            example = "ipfs://Qm...")
        String metadata) {}
