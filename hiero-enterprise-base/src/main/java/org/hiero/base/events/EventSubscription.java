package org.hiero.base.events;

/**
 * Handle for an active event subscription.
 */
public interface EventSubscription {

  /**
   * Unsubscribes the listener and stops the observation.
   */
  void unsubscribe();

  /**
   * Returns true if the subscription is still active.
   *
   * @return true if active
   */
  boolean isActive();
}
