package org.hiero.base.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.hedera.hashgraph.sdk.AccountId;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.hiero.base.data.Page;
import org.hiero.base.data.TransactionInfo;
import org.hiero.base.mirrornode.TransactionRepository;
import org.hiero.base.observer.EventObserver;
import org.hiero.base.observer.TransactionObserver;
import org.hiero.base.protocol.data.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionObserverTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private EventObserver<TransactionInfo> listener;

    private TransactionObserver observer;
    private final AccountId accountId = AccountId.fromString("0.0.1234");
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        observer = new TransactionObserver(executorService, repository, accountId, Duration.ofMillis(100), listener);
    }

    @Test
    void testPollDetectsNewTransactions() throws Exception {
        // Setup timestamps
        Instant now = Instant.now();
        Instant txTime = now.plusSeconds(1);
        
        TransactionInfo tx = new TransactionInfo(
                "0.0.1234@1234567890.000000001",
                null, 0, txTime, null, "100", null,
                TransactionType.CRYPTO_TRANSFER, Collections.emptyList(), null, 0, null,
                "SUCCESS", false, Collections.emptyList(), Collections.emptyList(), null,
                Collections.emptyList(), "120", Instant.now()
        );

        when(repository.findByAccount(eq(accountId), any(Instant.class)))
                .thenReturn(new Page<>() {
                    @Override public int getPageIndex() { return 0; }
                    @Override public int getSize() { return 1; }
                    @Override public List<TransactionInfo> getData() { return List.of(tx); }
                    @Override public boolean hasNext() { return false; }
                    @Override public Page<TransactionInfo> next() { return null; }
                    @Override public Page<TransactionInfo> first() { return this; }
                    @Override public boolean isFirst() { return true; }
                });

        // First poll should use start timestamp (now)
        observer.poll();

        verify(listener, times(1)).onEvent(tx);
    }

    @Test
    void testPollIgnoresDuplicates() throws Exception {
        Instant now = Instant.now();
        TransactionInfo tx = mock(TransactionInfo.class);
        when(tx.consensusTimestamp()).thenReturn(now.plusSeconds(1));

        when(repository.findByAccount(eq(accountId), any(Instant.class)))
                .thenReturn(new Page<>() {
                    @Override public int getPageIndex() { return 0; }
                    @Override public int getSize() { return 1; }
                    @Override public List<TransactionInfo> getData() { return List.of(tx); }
                    @Override public boolean hasNext() { return false; }
                    @Override public Page<TransactionInfo> next() { return null; }
                    @Override public Page<TransactionInfo> first() { return this; }
                    @Override public boolean isFirst() { return true; }
                });

        observer.poll();
        observer.poll();

        // Should only notify once if the repository filter works correctly
        verify(listener, times(1)).onEvent(tx);
    }
}
