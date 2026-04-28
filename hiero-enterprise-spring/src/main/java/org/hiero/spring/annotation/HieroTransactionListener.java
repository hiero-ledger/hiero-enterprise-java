package org.hiero.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as a listener for Hiero network transactions.
 *
 * <p>The annotated method should accept a single parameter of type {@code TransactionInfo}.
 *
 * <pre>{@code
 * @HieroTransactionListener(account = "0.0.1234")
 * public void onTransfer(TransactionInfo tx) {
 *     log.info("Received transaction: {}", tx.transactionId());
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HieroTransactionListener {

  /**
   * The account ID to monitor for transactions.
   *
   * @return the account ID (e.g., "0.0.1234").
   */
  String account();

  /**
   * Polling interval in milliseconds. Defaults to 5000ms (5 seconds).
   *
   * @return the polling interval.
   */
  long interval() default 5000;
}
