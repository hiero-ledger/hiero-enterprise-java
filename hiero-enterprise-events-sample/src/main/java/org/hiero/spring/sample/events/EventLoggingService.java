package org.hiero.spring.sample.events;

import com.hedera.hashgraph.sdk.ContractId;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hiero.base.events.ContractEvent;
import org.hiero.spring.events.HieroEventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventLoggingService {

  private static final Logger log = LoggerFactory.getLogger(EventLoggingService.class);

  private final HieroEventSubscriber eventSubscriber;
  private final List<ContractEvent> lastEvents = Collections.synchronizedList(new ArrayList<>());

  @Value("${app.contract-id:0.0.1234}")
  private String contractIdStr;

  public EventLoggingService(HieroEventSubscriber eventSubscriber) {
    this.eventSubscriber = eventSubscriber;
  }

  @PostConstruct
  public void init() {
    ContractId contractId = ContractId.fromString(contractIdStr);
    log.info("Subscribing to events for contract: {}", contractId);

    eventSubscriber.subscribe(contractId, event -> {
      log.info("Received event: {} from contract {}", event.eventName(), event.contractId());
      lastEvents.add(0, event);
      if (lastEvents.size() > 100) {
        lastEvents.remove(lastEvents.size() - 1);
      }
    });
  }

  public List<ContractEvent> getLastEvents() {
    return new ArrayList<>(lastEvents);
  }
}
