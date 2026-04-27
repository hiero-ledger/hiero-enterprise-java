package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.HookId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record HookStoreRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull HookId hookId,
    @NonNull List<EvmHookStorageUpdate> storageUpdates,
    @NonNull List<PrivateKey> signerKeys)
    implements TransactionRequest {

  public HookStoreRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration must not be null");
    Objects.requireNonNull(hookId, "hookId must not be null");
    Objects.requireNonNull(storageUpdates, "storageUpdates must not be null");
    Objects.requireNonNull(signerKeys, "signerKeys must not be null");
    signerKeys = List.copyOf(signerKeys);
    signerKeys.forEach(key -> Objects.requireNonNull(key, "signer key must not be null"));
  }

  public static HookStoreRequest of(
      @NonNull final HookId hookId,
      @NonNull final List<EvmHookStorageUpdate> storageUpdates,
      @NonNull final PrivateKey signerKey) {
    return of(hookId, storageUpdates, List.of(signerKey));
  }

  public static HookStoreRequest of(
      @NonNull final HookId hookId,
      @NonNull final List<EvmHookStorageUpdate> storageUpdates,
      @NonNull final List<PrivateKey> signerKeys) {
    return new HookStoreRequest(
        TransactionRequest.DEFAULT_MAX_TRANSACTION_FEE,
        TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION,
        hookId,
        storageUpdates,
        signerKeys);
  }
}
