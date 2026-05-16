package org.hiero.spring.implementation;

import com.hedera.hashgraph.sdk.Hbar;
import org.hiero.base.AccountClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Health indicator for the Hiero operator account balance.
 */
public class OperatorBalanceHealthIndicator implements HealthIndicator {

  private final AccountClient accountClient;
  private final long minBalanceInHbar;

  public OperatorBalanceHealthIndicator(
      final AccountClient accountClient, final long minBalanceInHbar) {
    this.accountClient = accountClient;
    this.minBalanceInHbar = minBalanceInHbar;
  }

  @Override
  public Health health() {
    try {
      final Hbar balance = accountClient.getOperatorAccountBalance();
      final long currentTinybars = balance.toTinybars();
      final long thresholdTinybars = minBalanceInHbar * 100_000_000L;

      Health.Builder builder = Health.up()
          .withDetail("balance", balance.toString())
          .withDetail("thresholdHbar", minBalanceInHbar);

      if (currentTinybars < thresholdTinybars) {
        return builder.down()
            .withDetail("status", "Low balance warning")
            .build();
      }

      return builder.withDetail("status", "Balance is sufficient").build();
    } catch (Exception e) {
      return Health.down()
          .withDetail("status", "Could not retrieve operator balance")
          .withDetail("reason", e.getMessage())
          .build();
    }
  }
}
