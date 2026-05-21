package org.hiero.base.implementation;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import java.util.function.Supplier;
import org.hiero.base.HieroException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Utility class for OpenTelemetry tracing. */
public final class TracingSupport {

  private static final String INSTRUMENTATION_NAME = "org.hiero.enterprise";

  private TracingSupport() {}

  /**
   * Get the tracer for the Hiero library.
   *
   * @return the tracer
   */
  @NonNull
  public static Tracer getTracer() {
    return GlobalOpenTelemetry.getTracer(INSTRUMENTATION_NAME);
  }

  /**
   * Execute a supplier within a new span.
   *
   * @param tracer the tracer to use
   * @param spanName the name of the span
   * @param supplier the supplier to execute
   * @param <T> the type of the result
   * @return the result
   */
  public static <T> T withinSpan(
      @NonNull final Tracer tracer,
      @NonNull final String spanName,
      @NonNull final Supplier<T> supplier) {
    return withinSpan(tracer, spanName, null, supplier);
  }

  /**
   * Execute a supplier within a new span with custom attributes.
   *
   * @param tracer the tracer to use
   * @param spanName the name of the span
   * @param attributesConfigurer a configurer for the span attributes
   * @param supplier the supplier to execute
   * @param <T> the type of the result
   * @return the result
   */
  public static <T> T withinSpan(
      @NonNull final Tracer tracer,
      @NonNull final String spanName,
      @Nullable final SpanAttributesConfigurer attributesConfigurer,
      @NonNull final Supplier<T> supplier) {
    final SpanBuilder spanBuilder = tracer.spanBuilder(spanName);
    if (attributesConfigurer != null) {
      attributesConfigurer.configure(spanBuilder);
    }
    final Span span = spanBuilder.startSpan();
    try (var ignored = span.makeCurrent()) {
      return supplier.get();
    } catch (final Exception e) {
      span.recordException(e);
      throw e;
    } finally {
      span.end();
    }
  }

  /**
   * Execute an operation that may throw a HieroException within a new span.
   *
   * @param tracer the tracer to use
   * @param spanName the name of the span
   * @param operation the operation to execute
   * @param <T> the type of the result
   * @return the result
   * @throws HieroException if the operation fails
   */
  public static <T> T withinSpanWithHieroException(
      @NonNull final Tracer tracer,
      @NonNull final String spanName,
      @NonNull final HieroOperation<T> operation)
      throws HieroException {
    final Span span = tracer.spanBuilder(spanName).startSpan();
    try (var ignored = span.makeCurrent()) {
      return operation.execute();
    } catch (final HieroException e) {
      span.recordException(e);
      throw e;
    } catch (final Exception e) {
      span.recordException(e);
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new HieroException("Error during traced operation", e);
    } finally {
      span.end();
    }
  }

  /** Interface for Hiero operations that can throw any exception. */
  @FunctionalInterface
  public interface HieroOperation<T> {
    T execute() throws Exception;
  }

  /** Interface to configure span attributes. */
  @FunctionalInterface
  public interface SpanAttributesConfigurer {
    void configure(@NonNull SpanBuilder spanBuilder);
  }
}
