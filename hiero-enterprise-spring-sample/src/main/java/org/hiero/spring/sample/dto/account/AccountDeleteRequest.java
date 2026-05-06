package org.hiero.spring.sample.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request to delete a Hiero account.
 */
@Schema(name = "Account: Delete Request", description = "Request DTO for deleting a Hiero account.")
public record AccountDeleteRequest(
    @Schema(description = "The ID of the account to delete.", example = "0.0.1234", requiredMode = Schema.RequiredMode.REQUIRED)
    String accountId,
    @Schema(description = "The private key of the account to delete.", example = "302e020100300506032b657004220420...", requiredMode = Schema.RequiredMode.REQUIRED)
    String privateKey,
    @Schema(description = "The ID of the account to transfer remaining funds to (optional, defaults to operator).", example = "0.0.5678", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String transferToAccountId
) {}
