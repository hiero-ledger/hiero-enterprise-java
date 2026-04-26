package org.hiero.base.observer;

import com.hedera.hashgraph.sdk.TopicId;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.hiero.base.data.Page;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.mirrornode.TopicRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer that polls for new messages on a specific Hiero Consensus Service (HCS) topic.
 */
public class TopicObserver extends AbstractPollingObserver<TopicMessage> {

    private static final Logger log = LoggerFactory.getLogger(TopicObserver.class);

    private final TopicRepository repository;
    private final TopicId topicId;
    private Instant lastSeenTimestamp;

    /**
     * Creates a new topic observer.
     *
     * @param repository The repository to use for polling.
     * @param topicId The topic to monitor.
     * @param pollingInterval How often to poll the mirror node.
     * @param listener The callback for new messages.
     */
    public TopicObserver(
            @NonNull TopicRepository repository,
            @NonNull TopicId topicId,
            @NonNull Duration pollingInterval,
            @NonNull EventObserver<TopicMessage> listener) {
        super(pollingInterval, listener);
        this.repository = repository;
        this.topicId = topicId;
        this.lastSeenTimestamp = Instant.now();
    }

    @Override
    public void poll() throws Exception {
        log.trace("Polling messages for topic {} after {}", topicId, lastSeenTimestamp);

        Page<TopicMessage> page = repository.getMessages(topicId, lastSeenTimestamp);
        List<TopicMessage> messages = new java.util.ArrayList<>(page.getData());

        if (messages.isEmpty()) {
            return;
        }

        // Sort by timestamp to ensure chronological delivery
        messages.sort(Comparator.comparing(TopicMessage::consensusTimestamp));

        for (TopicMessage msg : messages) {
            if (msg.consensusTimestamp().isAfter(lastSeenTimestamp)) {
                notifyListener(msg);
                lastSeenTimestamp = msg.consensusTimestamp();
            }
        }
    }

    /**
     * Sets the starting timestamp for the observer.
     *
     * @param timestamp The timestamp to start from.
     * @return this observer for chaining.
     */
    public TopicObserver startFrom(@NonNull Instant timestamp) {
        this.lastSeenTimestamp = timestamp;
        return this;
    }
}
