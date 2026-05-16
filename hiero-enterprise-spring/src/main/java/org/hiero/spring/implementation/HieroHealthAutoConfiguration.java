package org.hiero.spring.implementation;

import org.hiero.base.AccountClient;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Hiero health indicators.
 */
@AutoConfiguration
@ConditionalOnClass(HealthIndicator.class)
@ConditionalOnProperty(
    prefix = "spring.hiero.health",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class HieroHealthAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  public MirrorNodeHealthIndicator mirrorNodeHealthIndicator(final MirrorNodeClient mirrorNodeClient) {
    return new MirrorNodeHealthIndicator(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public OperatorBalanceHealthIndicator operatorBalanceHealthIndicator(
      final AccountClient accountClient, final HieroProperties properties) {
    return new OperatorBalanceHealthIndicator(
        accountClient, properties.getHealth().getMinBalanceInHbar());
  }
}
