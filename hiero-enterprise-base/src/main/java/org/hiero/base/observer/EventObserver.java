package org.hiero.base.observer;

/**
 * Functional interface for a component that observes events on the Hiero network.
 *
 * <p>Implementations of this interface typically run in a background thread or are triggered by a
 * scheduler to check for new network activities.
 *
 * @param <T> The type of event being observed (e.g., TransactionInfo, TopicMessage).
 */
@FunctionalInterface
public interface EventObserver<T> {

  /**
   * Called when a new event is detected.
   *
   * @param event The detected event.
   */
  void onEvent(T event);
}
