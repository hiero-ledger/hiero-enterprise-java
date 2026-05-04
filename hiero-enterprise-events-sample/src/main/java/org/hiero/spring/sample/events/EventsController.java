package org.hiero.spring.sample.events;

import java.util.List;
import org.hiero.base.events.ContractEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventsController {

  private final EventLoggingService eventLoggingService;

  public EventsController(EventLoggingService eventLoggingService) {
    this.eventLoggingService = eventLoggingService;
  }

  @GetMapping("/last")
  public List<ContractEvent> getLastEvents() {
    return eventLoggingService.getLastEvents();
  }
}
