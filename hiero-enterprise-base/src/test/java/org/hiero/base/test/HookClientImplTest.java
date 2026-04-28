package org.hiero.base.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.HookEntityId;
import com.hedera.hashgraph.sdk.HookId;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.util.List;
import org.hiero.base.HieroException;
import org.hiero.base.implementation.HookClientImpl;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.HookStoreRequest;
import org.hiero.base.protocol.data.HookStoreResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class HookClientImplTest {

  private ProtocolLayerClient protocolLayerClient;
  private HookClientImpl hookClient;
  private ArgumentCaptor<HookStoreRequest> hookStoreCaptor;

  @BeforeEach
  void setup() {
    protocolLayerClient = Mockito.mock(ProtocolLayerClient.class);
    hookClient = new HookClientImpl(protocolLayerClient);
    hookStoreCaptor = ArgumentCaptor.forClass(HookStoreRequest.class);
  }

  @Test
  void shouldStoreHookWithSingleSigner() throws HieroException {
    final HookStoreResult result = Mockito.mock(HookStoreResult.class);
    final HookId hookId = new HookId(new HookEntityId(new ContractId(0, 0, 1234)), 1L);
    final List<EvmHookStorageUpdate> storageUpdates =
        List.of(new EvmHookStorageUpdate.EvmHookStorageSlot(new byte[] {1}, new byte[] {2}));
    final PrivateKey signerKey = PrivateKey.generateED25519();

    when(protocolLayerClient.executeHookStoreTransaction(any(HookStoreRequest.class)))
        .thenReturn(result);

    hookClient.storeHook(hookId, storageUpdates, signerKey);

    verify(protocolLayerClient, times(1)).executeHookStoreTransaction(hookStoreCaptor.capture());

    final HookStoreRequest request = hookStoreCaptor.getValue();
    Assertions.assertEquals(hookId, request.hookId());
    Assertions.assertEquals(storageUpdates, request.storageUpdates());
    Assertions.assertEquals(List.of(signerKey), request.signerKeys());
  }

  @Test
  void shouldStoreHookWithMultipleSigners() throws HieroException {
    final HookStoreResult result = Mockito.mock(HookStoreResult.class);
    final HookId hookId = new HookId(new HookEntityId(new ContractId(0, 0, 1234)), 1L);
    final List<EvmHookStorageUpdate> storageUpdates =
        List.of(new EvmHookStorageUpdate.EvmHookStorageSlot(new byte[] {1}, new byte[] {2}));
    final PrivateKey signerOne = PrivateKey.generateED25519();
    final PrivateKey signerTwo = PrivateKey.generateED25519();
    final List<PrivateKey> signerKeys = List.of(signerOne, signerTwo);

    when(protocolLayerClient.executeHookStoreTransaction(any(HookStoreRequest.class)))
        .thenReturn(result);

    hookClient.storeHook(hookId, storageUpdates, signerKeys);

    verify(protocolLayerClient, times(1)).executeHookStoreTransaction(hookStoreCaptor.capture());

    final HookStoreRequest request = hookStoreCaptor.getValue();
    Assertions.assertEquals(hookId, request.hookId());
    Assertions.assertEquals(storageUpdates, request.storageUpdates());
    Assertions.assertEquals(signerKeys, request.signerKeys());
  }

  @Test
  void shouldThrowForNullParams() {
    final HookId hookId = new HookId(new HookEntityId(new ContractId(0, 0, 1234)), 1L);
    final List<EvmHookStorageUpdate> storageUpdates =
        List.of(new EvmHookStorageUpdate.EvmHookStorageSlot(new byte[] {1}, new byte[] {2}));
    final PrivateKey signerKey = PrivateKey.generateED25519();
    final List<PrivateKey> signerKeys = List.of(signerKey);

    Assertions.assertThrows(
        NullPointerException.class, () -> hookClient.storeHook(null, storageUpdates, signerKey));
    Assertions.assertThrows(
        NullPointerException.class, () -> hookClient.storeHook(hookId, null, signerKey));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> hookClient.storeHook(hookId, storageUpdates, (PrivateKey) null));
    Assertions.assertThrows(
        NullPointerException.class, () -> hookClient.storeHook(null, storageUpdates, signerKeys));
    Assertions.assertThrows(
        NullPointerException.class, () -> hookClient.storeHook(hookId, null, signerKeys));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> hookClient.storeHook(hookId, storageUpdates, (List<PrivateKey>) null));
  }
}
