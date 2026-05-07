package org.hiero.spring.sample.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request to create a new Hiero account. */
@Schema(
    name = "Account: Create Request",
    description = "Request DTO for creating a new Hiero account.")
public record AccountCreateRequest(
    @Schema(description = "The initial balance in Hbar (optional, defaults to 0).", example = "100")
        Long initialBalance) {}
