package org.hiero.spring.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TopicId;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.hiero.base.mirrornode.TopicRepository;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.observer.AbstractPollingObserver;
import org.hiero.base.observer.TopicObserver;
import org.hiero.base.observer.TransactionObserver;
import org.hiero.spring.annotation.HieroTopicListener;
import org.hiero.spring.annotation.HieroTransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ReflectionUtils;

/**
 * Processor that scans Spring beans for {@link HieroTransactionListener} and {@link
 * HieroTopicListener} annotations and manages the corresponding observers.
 */
public class HieroListenerProcessor implements BeanPostProcessor, SmartLifecycle {

  private static final Logger log = LoggerFactory.getLogger(HieroListenerProcessor.class);

  private final TransactionRepository transactionRepository;
  private final TopicRepository topicRepository;
  private final TaskScheduler taskScheduler;
  private final List<AbstractPollingObserver<?>> observers = new ArrayList<>();
  private boolean running = false;

  public HieroListenerProcessor(
      TransactionRepository transactionRepository, TopicRepository topicRepository) {
    this.transactionRepository = transactionRepository;
    this.topicRepository = topicRepository;
    this.taskScheduler = createDefaultTaskScheduler();
  }

  private TaskScheduler createDefaultTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
    scheduler.setThreadNamePrefix("hiero-observer-");
    scheduler.setDaemon(true);
    scheduler.initialize();
    return scheduler;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
    ReflectionUtils.doWithMethods(
        targetClass,
        method -> {
          if (method.isAnnotationPresent(HieroTransactionListener.class)) {
            processTransactionListener(bean, method);
          }
          if (method.isAnnotationPresent(HieroTopicListener.class)) {
            processTopicListener(bean, method);
          }
        });
    return bean;
  }

  private void processTransactionListener(Object bean, Method method) {
    HieroTransactionListener annotation = method.getAnnotation(HieroTransactionListener.class);
    log.info(
        "Registering transaction listener for account {} on method {}",
        annotation.account(),
        method.getName());

    TransactionObserver observer =
        new TransactionObserver(
            ((ThreadPoolTaskScheduler) taskScheduler).getScheduledExecutor(),
            transactionRepository,
            AccountId.fromString(annotation.account()),
            Duration.ofMillis(annotation.interval()),
            event -> invokeMethod(bean, method, event));
    observers.add(observer);
    if (running) {
      observer.start();
    }
  }

  private void processTopicListener(Object bean, Method method) {
    HieroTopicListener annotation = method.getAnnotation(HieroTopicListener.class);
    log.info(
        "Registering topic listener for topic {} on method {}",
        annotation.topicId(),
        method.getName());

    TopicObserver observer =
        new TopicObserver(
            ((ThreadPoolTaskScheduler) taskScheduler).getScheduledExecutor(),
            topicRepository,
            TopicId.fromString(annotation.topicId()),
            Duration.ofMillis(annotation.interval()),
            event -> invokeMethod(bean, method, event));
    observers.add(observer);
    if (running) {
      observer.start();
    }
  }

  private void invokeMethod(Object bean, Method method, Object event) {
    try {
      ReflectionUtils.makeAccessible(method);
      method.invoke(bean, event);
    } catch (Exception e) {
      log.error("Failed to invoke Hiero listener method {}", method.getName(), e);
    }
  }

  @Override
  public void start() {
    log.info("Starting Hiero listener observers...");
    observers.forEach(AbstractPollingObserver::start);
    this.running = true;
  }

  @Override
  public void stop() {
    log.info("Stopping Hiero listener observers...");
    observers.forEach(AbstractPollingObserver::stop);
    this.running = false;
  }

  @Override
  public boolean isRunning() {
    return running;
  }
}
