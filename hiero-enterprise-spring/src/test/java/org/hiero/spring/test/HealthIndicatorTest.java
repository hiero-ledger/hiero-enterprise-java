package org.hiero.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hedera.hashgraph.sdk.Hbar;
import org.hiero.base.AccountClient;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.spring.implementation.MirrorNodeHealthIndicator;
import org.hiero.spring.implementation.OperatorBalanceHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 * Unit tests for Hiero health indicators.
 */
@ExtendWith(MockitoExtension.class)
class HealthIndicatorTest {

  @Mock
  private MirrorNodeClient mirrorNodeClient;

  @Mock
  private AccountClient accountClient;

  @Test
  void testMirrorNodeHealthUp() throws Exception {
    MirrorNodeHealthIndicator indicator = new MirrorNodeHealthIndicator(mirrorNodeClient);
    Health health = indicator.health();
    assertEquals(Status.UP, health.getStatus());
    assertEquals("Mirror Node is reachable", health.getDetails().get("status"));
  }

  @Test
  void testMirrorNodeHealthDown() throws Exception {
    when(mirrorNodeClient.queryBlocks()).thenThrow(new RuntimeException("Connection failed"));
    MirrorNodeHealthIndicator indicator = new MirrorNodeHealthIndicator(mirrorNodeClient);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Mirror Node is unreachable", health.getDetails().get("status"));
  }

  @Test
  void testOperatorBalanceHealthUp() throws Exception {
    when(accountClient.getOperatorAccountBalance()).thenReturn(Hbar.from(20));
    OperatorBalanceHealthIndicator indicator = new OperatorBalanceHealthIndicator(accountClient, 10);
    Health health = indicator.health();
    assertEquals(Status.UP, health.getStatus());
    assertEquals("Balance is sufficient", health.getDetails().get("status"));
  }

  @Test
  void testOperatorBalanceHealthDown() throws Exception {
    when(accountClient.getOperatorAccountBalance()).thenReturn(Hbar.from(5));
    OperatorBalanceHealthIndicator indicator = new OperatorBalanceHealthIndicator(accountClient, 10);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Low balance warning", health.getDetails().get("status"));
  }

  @Test
  void testOperatorBalanceHealthError() throws Exception {
    when(accountClient.getOperatorAccountBalance()).thenThrow(new RuntimeException("API error"));
    OperatorBalanceHealthIndicator indicator = new OperatorBalanceHealthIndicator(accountClient, 10);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Could not retrieve operator balance", health.getDetails().get("status"));
  }
}
