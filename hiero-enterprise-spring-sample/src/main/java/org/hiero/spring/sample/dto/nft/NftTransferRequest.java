package org.hiero.spring.sample.dto.nft;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for transferring an NFT instance.
 */
@Schema(name = "Non-Fungible Token: Transfer Request", description = "Request DTO for transferring a specific NFT serial number between accounts.")
public record NftTransferRequest(
    @Schema(description = "The ID of the account transferring the NFT.", example = "0.0.1234", requiredMode = Schema.RequiredMode.REQUIRED)
    String fromAccountId,
    @Schema(description = "The private key of the account transferring the NFT.", example = "302e020100300506032b657004220420...", requiredMode = Schema.RequiredMode.REQUIRED)
    String fromPrivateKey,
    @Schema(description = "The ID of the account receiving the NFT.", example = "0.0.5678", requiredMode = Schema.RequiredMode.REQUIRED)
    String toAccountId,
    @Schema(description = "The serial number of the NFT instance to transfer.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    long serialNumber
) {}
