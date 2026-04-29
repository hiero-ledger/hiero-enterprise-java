package org.hiero.base.protocol.data;

import org.hiero.base.data.AccountInfo;
import org.jspecify.annotations.NonNull;

/** Response for an account info query. */
public record AccountInfoResponse(@NonNull AccountInfo accountInfo) {}
