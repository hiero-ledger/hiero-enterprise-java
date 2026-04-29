package org.hiero.base.data;

import java.util.List;

/** Data transfer object for token actions like minting, burning, or transferring. */
public record TokenActionRequest(
    Long amount, String metadata, String toAccountId, String accountKey, List<Long> serials) {}
