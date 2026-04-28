package org.hiero.base.observer;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for observers that poll the Mirror Node at regular intervals.
 *
 * <p>This class handles the lifecycle of the background polling task, including starting, stopping,
 * and handling unexpected errors during polling.
 *
 * @param <T> The type of event being observed.
 */
public abstract class AbstractPollingObserver<T> {

  private static final Logger log = LoggerFactory.getLogger(AbstractPollingObserver.class);

  private final ScheduledExecutorService executorService;
  private final Duration pollingInterval;
  private final EventObserver<T> listener;
  private final AtomicBoolean running = new AtomicBoolean(false);

  /**
   * Creates a new polling observer.
   *
   * @param executorService The shared executor service to use for polling.
   * @param pollingInterval How often to check for new events.
   * @param listener The callback to trigger when an event is found.
   */
  protected AbstractPollingObserver(
      @NonNull ScheduledExecutorService executorService,
      @NonNull Duration pollingInterval,
      @NonNull EventObserver<T> listener) {
    this.executorService = executorService;
    this.pollingInterval = pollingInterval;
    this.listener = listener;
  }

  /** Starts the background polling task. */
  public void start() {
    if (running.compareAndSet(false, true)) {
      log.info("Starting Hiero observer: {}", getClass().getSimpleName());
      scheduleNext(0);
    }
  }

  private void scheduleNext(long delayMs) {
    if (running.get()) {
      executorService.schedule(this::pollSafe, delayMs, TimeUnit.MILLISECONDS);
    }
  }

  /** Stops the background polling task. */
  public void stop() {
    running.set(false);
    log.info("Stopping Hiero observer: {}", getClass().getSimpleName());
  }

  private void pollSafe() {
    try {
      boolean hasMore = poll();
      // If there's more data (pagination), poll again immediately.
      // Otherwise, wait for the polling interval.
      scheduleNext(hasMore ? 0 : pollingInterval.toMillis());
    } catch (Exception e) {
      log.error("Unexpected error during Hiero polling in {}", getClass().getSimpleName(), e);
      scheduleNext(pollingInterval.toMillis());
    }
  }

  /**
   * The actual polling logic to be implemented by subclasses.
   *
   * @return true if there is more data to poll immediately (pagination), false otherwise.
   * @throws Exception if polling fails.
   */
  public abstract boolean poll() throws Exception;

  /**
   * Notifies the listener of a new event.
   *
   * @param event The event to notify.
   */
  protected void notifyListener(@NonNull T event) {
    try {
      listener.onEvent(event);
    } catch (Exception e) {
      log.error("Error in event listener callback", e);
    }
  }
}
