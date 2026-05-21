package org.hiero.spring.implementation;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * OpenTelemetry tracing support for Hiero.
 */
@AutoConfiguration
@ConditionalOnProperty(
    name = "spring.hiero.tracing.enabled",
    havingValue = "true",
    matchIfMissing = true)
@ConditionalOnClass(OpenTelemetry.class)
public class TracingSupportConfig {

  @Bean
  @NonNull
  public Tracer hieroTracer(@NonNull final OpenTelemetry openTelemetry) {
    return openTelemetry.getTracer("org.hiero.enterprise");
  }
}
