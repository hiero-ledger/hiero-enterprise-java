package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.hiero.base.events.EventFilter;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with smart contract logs on a Hiero network via Mirror Node.
 */
public interface ContractLogRepository {

  /**
   * Find logs for a specific contract.
   *
   * @param contractId the contract ID
   * @return a page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<ContractLog> findByContractId(@NonNull ContractId contractId) throws HieroException {
    return findByContractIdAndFilter(contractId, EventFilter.of(contractId));
  }

  /**
   * Find logs for a specific contract with additional filters.
   *
   * @param contractId the contract ID
   * @param filter the filter criteria
   * @return a page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  Page<ContractLog> findByContractIdAndFilter(@NonNull ContractId contractId, @NonNull EventFilter filter) throws HieroException;

  /**
   * Find logs for a specific contract.
   *
   * @param contractId the contract ID string
   * @return a page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<ContractLog> findByContractId(@NonNull String contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return findByContractId(ContractId.fromString(contractId));
  }
}
