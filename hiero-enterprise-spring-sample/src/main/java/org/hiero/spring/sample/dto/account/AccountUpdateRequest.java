package org.hiero.spring.sample.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request to update an existing Hiero account.
 */
@Schema(name = "Account: Update Request", description = "Request DTO for updating an existing Hiero account.")
public record AccountUpdateRequest(
    @Schema(description = "The ID of the account to update.", example = "0.0.1234", requiredMode = Schema.RequiredMode.REQUIRED)
    String accountId,
    @Schema(description = "The current private key of the account.", example = "302e020100300506032b657004220420...", requiredMode = Schema.RequiredMode.REQUIRED)
    String privateKey,
    @Schema(description = "The new private key to set (optional).", example = "302e020100300506032b657004220420...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String newPrivateKey,
    @Schema(description = "The new memo to set (optional).", example = "Updated account memo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String memo
) {}
