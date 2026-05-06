package org.hiero.spring.sample.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for token association requests.
 */
@Schema(name = "Fungible Token: Associate Request", description = "Request DTO for explicitly associating a Hiero account with a fungible token.")
public record TokenAssociateRequest(
    @Schema(description = "The ID of the account to associate with the token", example = "0.0.12345", requiredMode = Schema.RequiredMode.REQUIRED)
    String accountId,
    
    @Schema(description = "The private key of the account to sign the association", example = "302e...", requiredMode = Schema.RequiredMode.REQUIRED)
    String privateKey
) {}
