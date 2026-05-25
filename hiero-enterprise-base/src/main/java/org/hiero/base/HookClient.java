package org.hiero.base;

import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.HookId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.List;
import org.jspecify.annotations.NonNull;

/** Client API for hook-related transactions. */
public interface HookClient {

  /**
   * Store hook updates using a specific signer key.
   *
   * @param hookId hook identifier
   * @param storageUpdates storage updates to apply
   * @param signerKey signer key required by the hook
   * @throws HieroException on failure
   */
  void storeHook(
      @NonNull HookId hookId,
      @NonNull List<EvmHookStorageUpdate> storageUpdates,
      @NonNull PrivateKey signerKey)
      throws HieroException;

  /**
   * Store hook updates using specific signer keys.
   *
   * @param hookId hook identifier
   * @param storageUpdates storage updates to apply
   * @param signerKeys signer keys required by the hook
   * @throws HieroException on failure
   */
  void storeHook(
      @NonNull HookId hookId,
      @NonNull List<EvmHookStorageUpdate> storageUpdates,
      @NonNull List<PrivateKey> signerKeys)
      throws HieroException;
}
