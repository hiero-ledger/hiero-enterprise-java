package org.hiero.base.observer;

import com.hedera.hashgraph.sdk.AccountId;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.hiero.base.data.Page;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.TransactionRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer that polls for new transactions associated with a specific account.
 *
 * <p>This observer tracks the consensus timestamp of the last processed transaction
 * to ensure that only new transactions are delivered to the listener.
 */
public class TransactionObserver extends AbstractPollingObserver<TransactionInfo> {

    private static final Logger log = LoggerFactory.getLogger(TransactionObserver.class);

    private final TransactionRepository repository;
    private final AccountId accountId;
    private Instant lastSeenTimestamp;

    /**
     * Creates a new transaction observer.
     *
     * @param executorService The shared executor service to use for polling.
     * @param repository The repository to use for polling.
     * @param accountId The account to monitor.
     * @param pollingInterval How often to poll the mirror node.
     * @param listener The callback for new transactions.
     */
    public TransactionObserver(
            @NonNull ScheduledExecutorService executorService,
            @NonNull TransactionRepository repository,
            @NonNull AccountId accountId,
            @NonNull Duration pollingInterval,
            @NonNull EventObserver<TransactionInfo> listener) {
        super(executorService, pollingInterval, listener);
        this.repository = repository;
        this.accountId = accountId;
        // Start from current time to avoid processing historical transactions by default
        this.lastSeenTimestamp = Instant.now();
    }

    @Override
    public boolean poll() throws Exception {
        log.trace("Polling transactions for account {} after {}", accountId, lastSeenTimestamp);

        Page<TransactionInfo> page = repository.findByAccount(accountId, lastSeenTimestamp);
        processPage(page);

        return page.hasNext();
    }

    private void processPage(Page<TransactionInfo> page) {
        List<TransactionInfo> transactions = new java.util.ArrayList<>(page.getData());
        if (transactions.isEmpty()) {
            return;
        }

        // Sort by timestamp to ensure chronological delivery
        transactions.sort(Comparator.comparing(TransactionInfo::consensusTimestamp));

        for (TransactionInfo tx : transactions) {
            if (tx.consensusTimestamp().isAfter(lastSeenTimestamp)) {
                notifyListener(tx);
                lastSeenTimestamp = tx.consensusTimestamp();
            }
        }
    }

    /**
     * Sets the starting timestamp for the observer.
     *
     * @param timestamp The timestamp to start from.
     * @return this observer for chaining.
     */
    public TransactionObserver startFrom(@NonNull Instant timestamp) {
        this.lastSeenTimestamp = timestamp;
        return this;
    }
}
