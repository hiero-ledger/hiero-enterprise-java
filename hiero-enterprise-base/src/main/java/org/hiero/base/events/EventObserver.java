package org.hiero.base.events;

import org.hiero.base.HieroException;

/**
 * Interface for observing smart contract events.
 */
public interface EventObserver {

  /**
   * Subscribes to events based on a filter.
   *
   * @param filter the filter criteria
   * @param listener the listener to notify
   * @return the subscription object
   * @throws HieroException if subscription fails
   */
  EventSubscription subscribe(EventFilter filter, ContractEventListener listener) throws HieroException;

  /**
   * Unsubscribes from events for a specific contract.
   *
   * @param subscription the subscription to remove
   */
  void unsubscribe(EventSubscription subscription);
}
