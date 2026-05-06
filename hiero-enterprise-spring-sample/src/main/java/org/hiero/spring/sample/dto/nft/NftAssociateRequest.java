package org.hiero.spring.sample.dto.nft;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for associating an account with an NFT collection.
 */
@Schema(name = "Non-Fungible Token: Associate Request", description = "Request DTO for explicitly associating a Hiero account with an NFT collection.")
public record NftAssociateRequest(
    @Schema(description = "The ID of the account to associate.", example = "0.0.1234", requiredMode = Schema.RequiredMode.REQUIRED)
    String accountId,
    @Schema(description = "The private key of the account to associate.", example = "302e020100300506032b657004220420...", requiredMode = Schema.RequiredMode.REQUIRED)
    String privateKey
) {}
