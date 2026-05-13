package org.hiero.base.data;

import com.hedera.hashgraph.sdk.EvmHook;
import com.hedera.hashgraph.sdk.HookCreationDetails;
import com.hedera.hashgraph.sdk.HookExtensionPoint;
import com.hedera.hashgraph.sdk.Key;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Data model describing a hook to be created on an account update transaction. */
public record HookDetails(
    @NonNull HookExtensionPoint extensionPoint,
    long hookId,
    @NonNull EvmHook hook,
    @Nullable Key adminKey) {

  public HookDetails {
    Objects.requireNonNull(extensionPoint, "extensionPoint is required");
    Objects.requireNonNull(hook, "hook is required");
    if (hookId < 0) {
      throw new IllegalArgumentException("hookId must be non-negative");
    }
  }

  @NonNull
  public HookCreationDetails toHederaSdkType() {
    if (adminKey != null) {
      return new HookCreationDetails(extensionPoint, hookId, hook, adminKey);
    }
    return new HookCreationDetails(extensionPoint, hookId, hook);
  }
}
