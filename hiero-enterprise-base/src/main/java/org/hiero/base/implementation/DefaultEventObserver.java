package org.hiero.base.implementation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.hiero.base.events.ContractEvent;
import org.hiero.base.events.ContractEventListener;
import org.hiero.base.events.EventFilter;
import org.hiero.base.events.EventObserver;
import org.hiero.base.events.EventSubscription;
import org.hiero.base.mirrornode.EventRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a contract event observer that polls the Mirror Node.
 */
public class DefaultEventObserver implements EventObserver {

  private static final Logger log = LoggerFactory.getLogger(DefaultEventObserver.class);

  private final EventRepository eventRepository;
  private final List<SubscriptionImpl> subscriptions = new CopyOnWriteArrayList<>();
  private final ScheduledExecutorService executorService;
  private long pollIntervalMs = 5000;

  public DefaultEventObserver(
      @NonNull EventRepository eventRepository,
      @NonNull ScheduledExecutorService executorService) {
    this.eventRepository = Objects.requireNonNull(eventRepository, "eventRepository must not be null");
    this.executorService = Objects.requireNonNull(executorService, "executorService must not be null");
    startPolling();
  }

  @Override
  public EventSubscription subscribe(
      @NonNull EventFilter filter, @NonNull ContractEventListener listener) {
    SubscriptionImpl sub = new SubscriptionImpl(filter, listener);
    subscriptions.add(sub);
    return sub;
  }

  @Override
  public void unsubscribe(EventSubscription subscription) {
    if (subscription instanceof SubscriptionImpl sub) {
      sub.unsubscribe();
    }
  }

  private void startPolling() {
    executorService.scheduleAtFixedRate(this::poll, pollIntervalMs, pollIntervalMs, TimeUnit.MILLISECONDS);
  }

  private void poll() {
    for (SubscriptionImpl sub : subscriptions) {
      if (sub.isActive()) {
        try {
          EventFilter filterWithRange = sub.getFilterWithLastTimestamp();
          Page<ContractLog> logsPage = eventRepository.findLogs(filterWithRange);
          List<ContractLog> logs = logsPage.getData();
          
          for (ContractLog logEntry : logs) {
            // Decoding logic would go here. For PoC, we create a simplified event.
            ContractEvent event = decode(logEntry);
            sub.listener().onEvent(event);
            sub.updateLastTimestamp(logEntry.consensusTimestamp());
          }
        } catch (HieroException e) {
          log.error("Failed to poll logs for subscription: {}", sub.filter(), e);
        }
      }
    }
  }

  private ContractEvent decode(ContractLog logEntry) {
    // In a real implementation, we would use the ABI to decode the data and topics.
    // For this PoC, we provide the raw data in the parameters.
    return new ContractEvent(
        logEntry.contractId(),
        "UnknownEvent", // Simplified
        java.util.Map.of("data", logEntry.data(), "topics", logEntry.topics()),
        logEntry.consensusTimestamp(),
        logEntry.blockNumber(),
        logEntry.transactionHash()
    );
  }

  private class SubscriptionImpl implements EventSubscription {
    private final EventFilter filter;
    private final ContractEventListener listener;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private Instant lastTimestamp;

    public SubscriptionImpl(EventFilter filter, ContractEventListener listener) {
      this.filter = filter;
      this.listener = listener;
      this.lastTimestamp = filter.fromTimestamp();
    }

    public EventFilter getFilterWithLastTimestamp() {
      return new EventFilter(
          filter.contractId(),
          filter.eventSignatures(),
          lastTimestamp != null ? lastTimestamp : filter.fromTimestamp(),
          filter.toTimestamp(),
          filter.fromBlock(),
          filter.toBlock()
      );
    }

    public void updateLastTimestamp(Instant timestamp) {
      if (lastTimestamp == null || timestamp.isAfter(lastTimestamp)) {
        this.lastTimestamp = timestamp;
      }
    }

    @Override
    public void unsubscribe() {
      active.set(false);
      subscriptions.remove(this);
    }

    @Override
    public boolean isActive() {
      return active.get();
    }

    public ContractEventListener listener() {
      return listener;
    }

    public EventFilter filter() {
      return filter;
    }
  }
}
