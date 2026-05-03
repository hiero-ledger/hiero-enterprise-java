package org.hiero.spring.implementation;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Wraps Hiero clients and repositories in JDK proxies and records Micrometer metrics for each
 * public invocation.
 */
public class HieroMetricsBeanPostProcessor implements BeanPostProcessor {

  public static final String COUNTER_NAME = "hiero.calls";
  public static final String TIMER_NAME = "hiero.call.duration";
  public static final String COMPONENT_TAG = "hiero.component";
  public static final String TYPE_TAG = "hiero.type";
  public static final String METHOD_TAG = "hiero.method";
  public static final String OUTCOME_TAG = "hiero.outcome";

  private final MeterRegistry meterRegistry;

  public HieroMetricsBeanPostProcessor(@NonNull final MeterRegistry meterRegistry) {
    this.meterRegistry = Objects.requireNonNull(meterRegistry, "meterRegistry must not be null");
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    final Class<?> beanClass = bean.getClass();
    if (Proxy.isProxyClass(beanClass)) {
      return bean;
    }
    final List<Class<?>> trackedInterfaces = new ArrayList<>();
    for (Class<?> iface : beanClass.getInterfaces()) {
      if (isTrackedInterface(iface)) {
        trackedInterfaces.add(iface);
      }
    }
    if (trackedInterfaces.isEmpty()) {
      return bean;
    }
    final InvocationHandler handler = new MetricsInvocationHandler(bean, meterRegistry);
    return Proxy.newProxyInstance(
        beanClass.getClassLoader(), trackedInterfaces.toArray(new Class<?>[0]), handler);
  }

  private static boolean isTrackedInterface(@NonNull final Class<?> iface) {
    final String name = iface.getName();
    return name.startsWith("org.hiero.base.")
        && (name.endsWith("Client")
            || name.endsWith("Repository")
            || name.endsWith("ProtocolLayerClient"));
  }

  private static final class MetricsInvocationHandler implements InvocationHandler {

    private final Object target;
    private final MeterRegistry meterRegistry;

    private MetricsInvocationHandler(
        @NonNull final Object target, @NonNull final MeterRegistry meterRegistry) {
      this.target = Objects.requireNonNull(target, "target must not be null");
      this.meterRegistry = Objects.requireNonNull(meterRegistry, "meterRegistry must not be null");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass().equals(Object.class)) {
        return method.invoke(target, args);
      }
      final String type = method.getDeclaringClass().getSimpleName();
      final String component = type.endsWith("Repository") ? "repository" : "client";
      final String methodName = method.getName();
      final long start = System.nanoTime();
      try {
        final Object result = method.invoke(target, args);
        record(component, type, methodName, "success", start);
        return result;
      } catch (InvocationTargetException e) {
        record(component, type, methodName, "error", start);
        throw e.getCause();
      } catch (Exception e) {
        record(component, type, methodName, "error", start);
        throw e;
      }
    }

    private void record(
        @NonNull final String component,
        @NonNull final String type,
        @NonNull final String methodName,
        @NonNull final String outcome,
        final long startNanos) {
      final List<Tag> tags =
          List.of(
              Tag.of(COMPONENT_TAG, component),
              Tag.of(TYPE_TAG, type),
              Tag.of(METHOD_TAG, methodName),
              Tag.of(OUTCOME_TAG, outcome));
      final Counter counter = meterRegistry.counter(COUNTER_NAME, tags);
      counter.increment();
      final Timer timer = meterRegistry.timer(TIMER_NAME, tags);
      timer.record(System.nanoTime() - startNanos, TimeUnit.NANOSECONDS);
    }
  }
}
