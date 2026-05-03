package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hedera.hashgraph.sdk.EvmHookStorageUpdate;
import com.hedera.hashgraph.sdk.HookId;
import com.hedera.hashgraph.sdk.PrivateKey;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.hiero.base.HookClient;
import org.hiero.spring.implementation.HieroMetricsBeanPostProcessor;
import org.junit.jupiter.api.Test;

class HieroMetricsBeanPostProcessorTest {

  @Test
  void shouldCollectSuccessAndErrorMetrics() throws Exception {
    final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    final HieroMetricsBeanPostProcessor postProcessor =
        new HieroMetricsBeanPostProcessor(meterRegistry);
    final HookClient proxied =
        (HookClient)
            postProcessor.postProcessAfterInitialization(new TestClientImpl(), "testClient");

    proxied.storeHook(null, List.of(), PrivateKey.generateED25519());
    assertThrows(
        IllegalStateException.class,
        () -> proxied.storeHook(null, List.of(), List.of(PrivateKey.generateED25519())));

    assertEquals(
        1d,
        meterRegistry
            .get(HieroMetricsBeanPostProcessor.COUNTER_NAME)
            .tag(HieroMetricsBeanPostProcessor.TYPE_TAG, "HookClient")
            .tag(HieroMetricsBeanPostProcessor.METHOD_TAG, "storeHook")
            .tag(HieroMetricsBeanPostProcessor.OUTCOME_TAG, "success")
            .counter()
            .count());
    assertEquals(
        1d,
        meterRegistry
            .get(HieroMetricsBeanPostProcessor.COUNTER_NAME)
            .tag(HieroMetricsBeanPostProcessor.TYPE_TAG, "HookClient")
            .tag(HieroMetricsBeanPostProcessor.METHOD_TAG, "storeHook")
            .tag(HieroMetricsBeanPostProcessor.OUTCOME_TAG, "error")
            .counter()
            .count());
    assertEquals(
        1L,
        meterRegistry
            .get(HieroMetricsBeanPostProcessor.TIMER_NAME)
            .tag(HieroMetricsBeanPostProcessor.TYPE_TAG, "HookClient")
            .tag(HieroMetricsBeanPostProcessor.METHOD_TAG, "storeHook")
            .tag(HieroMetricsBeanPostProcessor.OUTCOME_TAG, "success")
            .timer()
            .count());
  }

  static class TestClientImpl implements HookClient {
    @Override
    public void storeHook(
        HookId hookId, List<EvmHookStorageUpdate> storageUpdates, PrivateKey signerKey) {}

    @Override
    public void storeHook(
        HookId hookId, List<EvmHookStorageUpdate> storageUpdates, List<PrivateKey> signerKeys) {
      throw new IllegalStateException("boom");
    }
  }
}
