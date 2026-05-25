package org.hiero.base.data;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.HookExtensionPoint;
import com.hedera.hashgraph.sdk.Key;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * High-level representation of a hook to attach to an account.
 *
 * @param extensionPoint the extension point where the hook should be attached
 * @param hookId unique identifier of the hook on the owning entity
 * @param evmHookContractId contract implementing the hook logic
 * @param initialStorageUpdates initial EVM storage updates to apply on hook creation
 * @param adminKey optional key used to authorize management operations for this hook
 */
public record HookDetails(
    @NonNull HookExtensionPoint extensionPoint,
    long hookId,
    @NonNull ContractId evmHookContractId,
    @NonNull List<EvmHookStorageUpdate> initialStorageUpdates,
    @Nullable Key adminKey) {

  public HookDetails {
    Objects.requireNonNull(extensionPoint, "extensionPoint must not be null");
    Objects.requireNonNull(evmHookContractId, "evmHookContractId must not be null");
    Objects.requireNonNull(initialStorageUpdates, "initialStorageUpdates must not be null");
    initialStorageUpdates.forEach(
        update -> Objects.requireNonNull(update, "initialStorageUpdates must not contain null"));
    initialStorageUpdates = List.copyOf(initialStorageUpdates);
    if (hookId < 0) {
      throw new IllegalArgumentException("hookId must be non-negative");
    }
  }
}
