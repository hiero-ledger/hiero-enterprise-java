package org.hiero.base.interceptors;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionResponse;
import org.jspecify.annotations.NonNull;

/**
 * Interceptor for executing a transaction. This interceptor is used to intercept the call for
 * executing a transaction. Frameworks like Spring can use this interceptor to add functionalities
 * like metrics, tracing, logging, or retry logic to the calls.
 */
@FunctionalInterface
public interface TransactionInterceptor {

  /**
   * Intercept the call for executing a transaction.
   *
   * @param chain the execution chain
   * @return the response for the transaction
   * @throws Exception if the execution fails
   */
  @NonNull TransactionResponse intercept(@NonNull Chain chain) throws Exception;

  /** Chain of execution for a transaction. */
  interface Chain {
    /**
     * Returns the transaction to execute.
     *
     * @return the transaction
     */
    @NonNull Transaction<?> transaction();

    /**
     * Returns the client to use for execution.
     *
     * @return the client
     */
    @NonNull Client client();

    /**
     * Proceeds with the execution of the transaction.
     *
     * @param transaction the transaction to execute
     * @return the response for the transaction
     * @throws Exception if the execution fails
     */
    @NonNull TransactionResponse proceed(@NonNull Transaction<?> transaction) throws Exception;
  }
}
