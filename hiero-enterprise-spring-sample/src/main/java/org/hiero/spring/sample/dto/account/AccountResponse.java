package org.hiero.spring.sample.dto.account;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response containing Hiero account details.
 *
 * @param accountId The ID of the account.
 * @param publicKey The public key of the account.
 * @param privateKey The private key of the account.
 */
@Schema(name = "Account: Response", description = "Response containing Hiero account details.")
public record AccountResponse(
    @Schema(description = "The ID of the account.")
    String accountId,
    @Schema(description = "The public key of the account.")
    String publicKey,
    @Schema(description = "The private key of the account.")
    String privateKey
) {}
