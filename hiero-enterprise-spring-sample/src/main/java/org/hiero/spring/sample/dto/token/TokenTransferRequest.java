package org.hiero.spring.sample.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;

/** Request to transfer Hiero fungible tokens between accounts. */
@Schema(
    name = "Fungible Token: Transfer Request",
    description = "Request DTO for transferring units of a fungible token between accounts.")
public record TokenTransferRequest(
    @Schema(
            description = "The ID of the account to transfer tokens from.",
            example = "0.0.1234",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String fromAccountId,
    @Schema(
            description = "The private key of the sender account.",
            example = "302e020100300506032b657004220420...",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String fromPrivateKey,
    @Schema(
            description = "The ID of the account to transfer tokens to.",
            example = "0.0.5678",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String toAccountId,
    @Schema(
            description = "The amount of tokens to transfer.",
            example = "1000",
            requiredMode = Schema.RequiredMode.REQUIRED)
        long amount) {}
