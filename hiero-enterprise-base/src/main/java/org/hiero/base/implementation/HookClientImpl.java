package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.HookId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.List;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.HookClient;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.HookStoreRequest;
import org.jspecify.annotations.NonNull;

public class HookClientImpl implements HookClient {

  private final ProtocolLayerClient client;

  public HookClientImpl(@NonNull final ProtocolLayerClient client) {
    this.client = Objects.requireNonNull(client, "client must not be null");
  }

  @Override
  public void storeHook(
      @NonNull final HookId hookId,
      @NonNull final List<EvmHookStorageUpdate> storageUpdates,
      @NonNull final PrivateKey signerKey)
      throws HieroException {
    Objects.requireNonNull(signerKey, "signerKey must not be null");
    storeHook(hookId, storageUpdates, List.of(signerKey));
  }

  @Override
  public void storeHook(
      @NonNull final HookId hookId,
      @NonNull final List<EvmHookStorageUpdate> storageUpdates,
      @NonNull final List<PrivateKey> signerKeys)
      throws HieroException {
    Objects.requireNonNull(hookId, "hookId must not be null");
    Objects.requireNonNull(storageUpdates, "storageUpdates must not be null");
    Objects.requireNonNull(signerKeys, "signerKeys must not be null");
    client.executeHookStoreTransaction(HookStoreRequest.of(hookId, storageUpdates, signerKeys));
  }
}
