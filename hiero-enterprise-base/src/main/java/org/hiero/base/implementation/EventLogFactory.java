package org.hiero.base.implementation;

import java.util.List;
import java.util.Objects;
import org.hiero.base.data.EventLog;
import org.jspecify.annotations.NonNull;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;

/**
 * Internal factory that creates {@link EventLog} instances from the Web3j {@link Log} type. This
 * class is part of the implementation and not of the public API, so that the Web3j {@code Log} type
 * does not leak into the public API.
 */
public final class EventLogFactory {

  private EventLogFactory() {}

  /**
   * Creates an {@link EventLog} by copying the raw fields of the given Web3j log and attaching the
   * already decoded parameter values.
   *
   * @param log the raw log received from Web3j
   * @param indexedValues the decoded values of the indexed event parameters
   * @param nonIndexedValues the decoded values of the non-indexed event parameters
   * @return the event log
   * @throws NullPointerException if one of the arguments or one of the mandatory log fields (for
   *     example the block hash of a pending log) is {@code null}
   */
  @NonNull
  public static EventLog create(
      @NonNull final Log log,
      @NonNull final List<Type> indexedValues,
      @NonNull final List<Type> nonIndexedValues) {
    Objects.requireNonNull(log, "log must not be null");
    return new EventLog(
        log.getAddress(),
        log.getTopics(),
        log.getBlockHash(),
        log.getLogIndex(),
        log.getBlockNumber(),
        log.getTransactionHash(),
        log.getTransactionIndex(),
        log.getData(),
        indexedValues,
        nonIndexedValues);
  }
}
