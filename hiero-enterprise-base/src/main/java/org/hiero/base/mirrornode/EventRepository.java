package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.ContractId;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.hiero.base.events.EventFilter;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with contract logs on a Hiero network via Mirror Node.
 */
public interface EventRepository {

  /**
   * Find contract logs based on the provided filter.
   *
   * @param filter the filter criteria
   * @return a page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  Page<ContractLog> findLogs(@NonNull EventFilter filter) throws HieroException;

  /**
   * Find logs for a specific contract.
   *
   * @param contractId the contract ID
   * @return a page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<ContractLog> findByContract(@NonNull ContractId contractId) throws HieroException {
    return findLogs(EventFilter.of(contractId));
  }
}
