package org.hiero.microprofile.implementation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Objects;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.Timer;
import org.jspecify.annotations.NonNull;

/**
 * Creates dynamic proxies that measure invocation count and latency for Hiero clients and
 * repositories.
 */
public class HieroMetricsProxyFactory {

  public static final String COUNTER_NAME = "hiero_calls_total";
  public static final String TIMER_NAME = "hiero_call_duration";
  public static final String COMPONENT_TAG = "hiero.component";
  public static final String TYPE_TAG = "hiero.type";
  public static final String METHOD_TAG = "hiero.method";
  public static final String OUTCOME_TAG = "hiero.outcome";

  private final MetricRegistry metricRegistry;

  public HieroMetricsProxyFactory(@NonNull final MetricRegistry metricRegistry) {
    this.metricRegistry = Objects.requireNonNull(metricRegistry, "metricRegistry must not be null");
  }

  @SuppressWarnings("unchecked")
  public <T> T createProxy(
      @NonNull final T target,
      @NonNull final Class<T> interfaceType,
      @NonNull final String component) {
    Objects.requireNonNull(target, "target must not be null");
    Objects.requireNonNull(interfaceType, "interfaceType must not be null");
    Objects.requireNonNull(component, "component must not be null");
    final InvocationHandler handler =
        new MetricsInvocationHandler(target, metricRegistry, component);
    return (T)
        Proxy.newProxyInstance(
            interfaceType.getClassLoader(), new Class<?>[] {interfaceType}, handler);
  }

  private static final class MetricsInvocationHandler implements InvocationHandler {
    private final Object target;
    private final MetricRegistry metricRegistry;
    private final String component;

    private MetricsInvocationHandler(
        @NonNull final Object target,
        @NonNull final MetricRegistry metricRegistry,
        @NonNull final String component) {
      this.target = Objects.requireNonNull(target, "target must not be null");
      this.metricRegistry =
          Objects.requireNonNull(metricRegistry, "metricRegistry must not be null");
      this.component = Objects.requireNonNull(component, "component must not be null");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass().equals(Object.class)) {
        return method.invoke(target, args);
      }
      final String type = method.getDeclaringClass().getSimpleName();
      final String methodName = method.getName();
      final long start = System.nanoTime();
      try {
        final Object result = method.invoke(target, args);
        record(type, methodName, "success", start);
        return result;
      } catch (InvocationTargetException e) {
        record(type, methodName, "error", start);
        throw e.getCause();
      } catch (Exception e) {
        record(type, methodName, "error", start);
        throw e;
      }
    }

    private void record(
        @NonNull final String type,
        @NonNull final String methodName,
        @NonNull final String outcome,
        final long startNanos) {
      final Tag[] tags =
          new Tag[] {
            new Tag(COMPONENT_TAG, component),
            new Tag(TYPE_TAG, type),
            new Tag(METHOD_TAG, methodName),
            new Tag(OUTCOME_TAG, outcome)
          };
      final Counter counter = metricRegistry.counter(COUNTER_NAME, tags);
      counter.inc();
      final Timer timer = metricRegistry.timer(TIMER_NAME, tags);
      timer.update(Duration.ofNanos(System.nanoTime() - startNanos));
    }
  }
}
