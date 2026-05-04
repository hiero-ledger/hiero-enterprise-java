package org.hiero.base.events;

import org.jspecify.annotations.NonNull;

/**
 * Functional interface for listening to smart contract events.
 */
@FunctionalInterface
public interface ContractEventListener {

  /**
   * Called when a new contract event is observed and decoded.
   *
   * @param event the observed event
   */
  void onEvent(@NonNull ContractEvent event);
}
