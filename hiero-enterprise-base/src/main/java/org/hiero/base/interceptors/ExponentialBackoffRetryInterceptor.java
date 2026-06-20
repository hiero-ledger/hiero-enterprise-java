package org.hiero.base.interceptors;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionResponse;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor that retries transaction execution on transient failures with exponential backoff.
 */
public class ExponentialBackoffRetryInterceptor implements TransactionInterceptor {

  private static final Logger log =
      LoggerFactory.getLogger(ExponentialBackoffRetryInterceptor.class);

  private static final int DEFAULT_MAX_RETRIES = 5;
  private static final long DEFAULT_INITIAL_BACKOFF_MS = 200;
  private static final long DEFAULT_MAX_BACKOFF_MS = 8000;

  private static final Set<Status> RETRYABLE_STATUSES =
      Set.of(Status.BUSY, Status.PLATFORM_TRANSACTION_NOT_CREATED, Status.PLATFORM_NOT_ACTIVE);

  private final int maxRetries;
  private final long initialBackoffMs;
  private final long maxBackoffMs;

  public ExponentialBackoffRetryInterceptor() {
    this(DEFAULT_MAX_RETRIES, DEFAULT_INITIAL_BACKOFF_MS, DEFAULT_MAX_BACKOFF_MS);
  }

  public ExponentialBackoffRetryInterceptor(
      int maxRetries, long initialBackoffMs, long maxBackoffMs) {
    this.maxRetries = maxRetries;
    this.initialBackoffMs = initialBackoffMs;
    this.maxBackoffMs = maxBackoffMs;
  }

  @Override
  @NonNull
  public TransactionResponse intercept(@NonNull Chain chain) throws Exception {
    int attempt = 0;
    long backoff = initialBackoffMs;
    Transaction<?> transaction = chain.transaction();

    while (true) {
      try {
        return chain.proceed(transaction);
      } catch (PrecheckStatusException e) {
        if (isRetryable(e.status) && attempt < maxRetries) {
          attempt++;
          log.warn(
              "Transaction execution failed with retryable status {}. Attempt {} of {}. Retrying in {}ms...",
              e.status,
              attempt,
              maxRetries,
              backoff);
          TimeUnit.MILLISECONDS.sleep(backoff);
          backoff = Math.min(backoff * 2, maxBackoffMs);
        } else {
          throw e;
        }
      } catch (Exception e) {
        // For other exceptions (like network issues), we might also want to retry
        // but for now let's stick to the requested BUSY status
        throw e;
      }
    }
  }

  private boolean isRetryable(Status status) {
    return RETRYABLE_STATUSES.contains(status);
  }
}
