package org.hiero.spring.sample.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request to create a new Hiero fungible token.
 */
@Schema(name = "Fungible Token: Create Request", description = "Request DTO for creating a new Hiero fungible token.")
public record TokenCreateRequest(
    @Schema(description = "The name of the token.", example = "Hiero Gold")
    String name,
    @Schema(description = "The symbol of the token.", example = "GOLD")
    String symbol,
    @Schema(description = "The number of decimals for the token (optional, defaults to 0).", example = "8")
    Integer decimals,
    @Schema(description = "The initial supply of the token (optional, defaults to 0).", example = "1000000")
    Long initialSupply
) {}
