package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Key;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * High-level representation of a hook to attach to an account.
 *
 * @param extensionPoint the extension point where the hook should be attached
 * @param hookId unique identifier of the hook on the owning entity
 * @param evmHookContractId contract implementing the hook logic
 * @param adminKey optional key used to authorize management operations for this hook
 */
public record HookDetails(
    @NonNull HookExtensionPoint extensionPoint,
    long hookId,
    @NonNull ContractId evmHookContractId,
    @Nullable Key adminKey) {

  public HookDetails {
    Objects.requireNonNull(extensionPoint, "extensionPoint must not be null");
    Objects.requireNonNull(evmHookContractId, "evmHookContractId must not be null");
    if (hookId < 0) {
      throw new IllegalArgumentException("hookId must be non-negative");
    }
  }
}
