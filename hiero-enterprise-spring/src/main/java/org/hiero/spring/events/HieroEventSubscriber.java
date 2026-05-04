package org.hiero.spring.events;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import org.hiero.base.events.ContractEventListener;
import org.hiero.base.events.EventFilter;
import org.hiero.base.events.EventObserver;
import org.hiero.base.events.EventSubscription;
import org.hiero.base.HieroException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

/**
 * Spring service to subscribe to smart contract events.
 */
@Service
public class HieroEventSubscriber {

  private final EventObserver eventObserver;

  public HieroEventSubscriber(@NonNull final EventObserver eventObserver) {
    this.eventObserver = Objects.requireNonNull(eventObserver, "eventObserver must not be null");
  }

  /**
   * Subscribe to events for a specific contract.
   *
   * @param contractId the contract ID
   * @param listener the listener callback
   * @return the subscription handle
   */
  public EventSubscription subscribe(
      @NonNull final ContractId contractId, @NonNull final ContractEventListener listener) {
    return this.subscribe(EventFilter.of(contractId), listener);
  }

  /**
   * Subscribe to events for a specific contract and event signature.
   *
   * @param contractId the contract ID
   * @param eventSignature the event signature (e.g. "Transfer(address,address,uint256)")
   * @param listener the listener callback
   * @return the subscription handle
   */
  public EventSubscription subscribe(
      @NonNull final ContractId contractId,
      @NonNull final String eventSignature,
      @NonNull final ContractEventListener listener) {
    return this.subscribe(EventFilter.of(contractId, eventSignature), listener);
  }

  /**
   * Subscribe with a custom filter.
   *
   * @param filter the event filter
   * @param listener the listener callback
   * @return the subscription handle
   */
  public EventSubscription subscribe(
      @NonNull final EventFilter filter, @NonNull final ContractEventListener listener) {
    try {
      return eventObserver.subscribe(filter, listener);
    } catch (HieroException e) {
      throw new RuntimeException("Failed to subscribe to events", e);
    }
  }
}
