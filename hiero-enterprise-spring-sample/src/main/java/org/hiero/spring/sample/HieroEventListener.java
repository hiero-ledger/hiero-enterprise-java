package org.hiero.spring.sample;

import org.hiero.base.data.TopicMessage;
import org.hiero.base.data.TransactionInfo;
import org.hiero.spring.annotation.HieroTopicListener;
import org.hiero.spring.annotation.HieroTransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HieroEventListener {

  private static final Logger log = LoggerFactory.getLogger(HieroEventListener.class);

  /**
   * Listen for transactions on a specific account. Note: In a real app, this would be an account
   * you care about (e.g., your treasury).
   */
  @HieroTransactionListener(account = "0.0.1234", interval = 10000)
  public void onTransaction(TransactionInfo tx) {
    log.info(
        ">>> [Hiero Event] New transaction detected: {} (Result: {})",
        tx.transactionId(),
        tx.result());
  }

  /** Listen for messages on a specific HCS topic. */
  @HieroTopicListener(topicId = "0.0.5678", interval = 5000)
  public void onTopicMessage(TopicMessage msg) {
    log.info(">>> [Hiero Event] New message on topic {}: {}", msg.topicId(), msg.message());
  }
}
