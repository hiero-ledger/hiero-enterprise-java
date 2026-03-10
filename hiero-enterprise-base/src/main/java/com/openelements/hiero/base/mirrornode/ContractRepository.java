package com.openelements.hiero.base.mirrornode;

import com.hedera.hashgraph.sdk.ContractId;
import com.openelements.hiero.base.HieroException;
import com.openelements.hiero.base.data.Contract;
import com.openelements.hiero.base.data.ContractLog;
import com.openelements.hiero.base.data.Order;
import com.openelements.hiero.base.data.Page;
import com.openelements.hiero.smartcontract.abi.model.AbiEvent;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
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

  default Page<ContractLog> findLogsByContract(@NonNull String contractId) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId));
  }

  default Page<ContractLog> findLogsByContract(@NonNull String contractId, @NonNull Order order) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), order);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull String contractId, @NonNull AbiEvent abiEvent) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), abiEvent);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull String contractId, @NonNull AbiEvent abiEvent, @NonNull Order order) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), abiEvent, order);
  }

  default Page<ContractLog> findLogsByContract(@NonNull String contractId, int pageLimit) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), pageLimit);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull String contractId, @NonNull Order order, int pageLimit) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), order, pageLimit);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull String contractId, @NonNull AbiEvent abiEvent, int pageLimit) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), abiEvent, pageLimit);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull String contractId, @NonNull AbiEvent abiEvent, @NonNull Order order, int pageLimit) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContract(ContractId.fromString(contractId), abiEvent, order, pageLimit);
  }

  default Page<ContractLog> findLogsByContract(@NonNull ContractId contractId) {
    return findLogsByContract(contractId, Order.DESC);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull ContractId contractId, @NonNull AbiEvent abiEvent) {
    return findLogsByContract(contractId, abiEvent, Order.DESC);
  }

  default Page<ContractLog> findLogsByContract(@NonNull ContractId contractId, int pageLimit) {
    return findLogsByContract(contractId, Order.DESC, pageLimit);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull ContractId contractId, @NonNull AbiEvent abiEvent, int pageLimit) {
    return findLogsByContract(contractId, abiEvent, Order.DESC, pageLimit);
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull ContractId contractId, @NonNull Order order) {
    throw new UnsupportedOperationException("findLogsByContract not yet implemented");
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull ContractId contractId, @NonNull AbiEvent abiEvent, @NonNull Order order) {
    throw new UnsupportedOperationException("findLogsByContract not yet implemented");
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull ContractId contractId, @NonNull Order order, int pageLimit) {
    throw new UnsupportedOperationException("findLogsByContract not yet implemented");
  }

  default Page<ContractLog> findLogsByContract(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      int pageLimit) {
    throw new UnsupportedOperationException("findLogsByContract not yet implemented");
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(ContractId.fromString(contractId), timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId, @NonNull Order order, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(ContractId.fromString(contractId), order, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId, @NonNull AbiEvent abiEvent, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(
        ContractId.fromString(contractId), abiEvent, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(
        ContractId.fromString(contractId), abiEvent, order, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId, int pageLimit, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(
        ContractId.fromString(contractId), pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId, @NonNull Order order, int pageLimit, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(
        ContractId.fromString(contractId), order, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId,
      @NonNull AbiEvent abiEvent,
      int pageLimit,
      ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(
        ContractId.fromString(contractId), abiEvent, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull String contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      int pageLimit,
      ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractBeforeTimestamp(
        ContractId.fromString(contractId), abiEvent, order, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId, ZonedDateTime timestamp) {
    return findLogsByContractBeforeTimestamp(contractId, Order.DESC, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId, @NonNull AbiEvent abiEvent, ZonedDateTime timestamp) {
    return findLogsByContractBeforeTimestamp(contractId, abiEvent, Order.DESC, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId, int pageLimit, ZonedDateTime timestamp) {
    return findLogsByContractBeforeTimestamp(contractId, Order.DESC, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      int pageLimit,
      ZonedDateTime timestamp) {
    return findLogsByContractBeforeTimestamp(
        contractId, abiEvent, Order.DESC, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId, @NonNull Order order, ZonedDateTime timestamp) {
    throw new UnsupportedOperationException(
        "findLogsByContractBeforeTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      ZonedDateTime timestamp) {
    throw new UnsupportedOperationException(
        "findLogsByContractBeforeTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId,
      @NonNull Order order,
      int pageLimit,
      ZonedDateTime timestamp) {
    throw new UnsupportedOperationException(
        "findLogsByContractBeforeTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractBeforeTimestamp(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      int pageLimit,
      ZonedDateTime timestamp) {
    throw new UnsupportedOperationException(
        "findLogsByContractBeforeTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(ContractId.fromString(contractId), timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId, @NonNull Order order, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(ContractId.fromString(contractId), order, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId, @NonNull AbiEvent abiEvent, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(ContractId.fromString(contractId), abiEvent, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(
        ContractId.fromString(contractId), abiEvent, order, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId, int pageLimit, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(
        ContractId.fromString(contractId), pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId, @NonNull Order order, int pageLimit, ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(
        ContractId.fromString(contractId), order, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId,
      @NonNull AbiEvent abiEvent,
      int pageLimit,
      ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(
        ContractId.fromString(contractId), abiEvent, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull String contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      int pageLimit,
      ZonedDateTime timestamp) {
    Objects.requireNonNull(contractId, "contractId must be provided");
    return findLogsByContractAfterTimestamp(
        ContractId.fromString(contractId), abiEvent, order, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId, ZonedDateTime timestamp) {
    return findLogsByContractAfterTimestamp(contractId, Order.DESC, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId, @NonNull AbiEvent abiEvent, ZonedDateTime timestamp) {
    return findLogsByContractAfterTimestamp(contractId, abiEvent, Order.DESC, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId, int pageLimit, ZonedDateTime timestamp) {
    return findLogsByContractAfterTimestamp(contractId, Order.DESC, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      int pageLimit,
      ZonedDateTime timestamp) {
    return findLogsByContractAfterTimestamp(contractId, abiEvent, Order.DESC, pageLimit, timestamp);
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId, @NonNull Order order, ZonedDateTime timestamp) {
    throw new UnsupportedOperationException("findLogsByContractAfterTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      ZonedDateTime timestamp) {
    throw new UnsupportedOperationException("findLogsByContractAfterTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId,
      @NonNull Order order,
      int pageLimit,
      ZonedDateTime timestamp) {
    throw new UnsupportedOperationException("findLogsByContractAfterTimestamp not yet implemented");
  }

  default Page<ContractLog> findLogsByContractAfterTimestamp(
      @NonNull ContractId contractId,
      @NonNull AbiEvent abiEvent,
      @NonNull Order order,
      int pageLimit,
      ZonedDateTime timestamp) {
    throw new UnsupportedOperationException("findLogsByContractAfterTimestamp not yet implemented");
  }
}
