package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

/**
 * Interface for querying contract event logs from the Hiero Mirror Node. This interface provides
 * methods for retrieving logs emitted by smart contracts.
 */
public interface ContractLogRepository {

  /**
   * Return all logs emitted by the given contract.
   *
   * @param contractId id of the contract
   * @return first page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull Page<ContractLog> findByContractId(@NonNull ContractId contractId) throws HieroException;

  /**
   * Return all logs emitted by the given contract.
   *
   * @param contractId id of the contract as a string
   * @return first page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<ContractLog> findByContractId(@NonNull String contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return findByContractId(ContractId.fromString(contractId));
  }
}
