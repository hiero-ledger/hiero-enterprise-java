package org.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.ContractId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Contract;
import org.hiero.base.data.ContractLog;
import org.hiero.base.data.ContractResult;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

/**
 * Interface for interacting with smart contracts on a Hiero network. This interface provides
 * methods for searching for contracts.
 */
public interface ContractRepository {

  /**
   * Return all contracts.
   *
   * @return first page of contracts
   * @throws HieroException if the search fails
   */
  @NonNull Page<Contract> findAll() throws HieroException;

  /**
   * Return a contract by its contract ID.
   *
   * @param contractId id of the contract
   * @return {@link Optional} containing the found contract or null
   * @throws HieroException if the search fails
   */
  @NonNull Optional<Contract> findById(@NonNull ContractId contractId) throws HieroException;

  /**
   * Return a contract by its contract ID.
   *
   * @param contractId id of the contract
   * @return {@link Optional} containing the found contract or null
   * @throws HieroException if the search fails
   */
  @NonNull
  default Optional<Contract> findById(@NonNull String contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return findById(ContractId.fromString(contractId));
  }

  /**
   * Return contract execution results by contract ID.
   *
   * @param contractId id of the contract
   * @return first page of contract execution results
   * @throws HieroException if the search fails
   */
  @NonNull Page<ContractResult> findResultsById(@NonNull ContractId contractId)
      throws HieroException;

  /**
   * Return contract execution results by contract ID.
   *
   * @param contractId id of the contract
   * @return first page of contract execution results
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<ContractResult> findResultsById(@NonNull String contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return findResultsById(ContractId.fromString(contractId));
  }

  /**
   * Return contract logs by contract ID.
   *
   * @param contractId id of the contract
   * @return first page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull Page<ContractLog> findLogsById(@NonNull ContractId contractId) throws HieroException;

  /**
   * Return contract logs by contract ID.
   *
   * @param contractId id of the contract
   * @return first page of contract logs
   * @throws HieroException if the search fails
   */
  @NonNull
  default Page<ContractLog> findLogsById(@NonNull String contractId) throws HieroException {
    Objects.requireNonNull(contractId, "contractId must not be null");
    return findLogsById(ContractId.fromString(contractId));
  }
}
