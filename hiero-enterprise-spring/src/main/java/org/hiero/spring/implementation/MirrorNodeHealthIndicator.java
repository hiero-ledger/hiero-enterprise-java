package org.hiero.spring.implementation;

import org.hiero.base.mirrornode.MirrorNodeClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Health indicator for the Hiero Mirror Node.
 */
public class MirrorNodeHealthIndicator implements HealthIndicator {

  private final MirrorNodeClient mirrorNodeClient;

  public MirrorNodeHealthIndicator(final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient = mirrorNodeClient;
  }

  @Override
  public Health health() {
    try {
      // Perform a simple query to verify connectivity
      mirrorNodeClient.queryBlocks();
      return Health.up()
          .withDetail("status", "Mirror Node is reachable")
          .build();
    } catch (Exception e) {
      return Health.down()
          .withDetail("status", "Mirror Node is unreachable")
          .withDetail("reason", e.getMessage())
          .build();
    }
  }
}
