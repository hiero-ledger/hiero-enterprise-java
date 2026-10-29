package org.hiero.base.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.web3j.abi.datatypes.Type;

/**
 * Represents a decoded smart contract event (log entry) that has been emitted by a contract and
 * received from the network.
 *
 * <p>The raw log fields (address, topics, block and transaction information, data) are provided
 * as-is. In addition, the parameters of the event are decoded and split into {@link
 * #indexedValues()} and {@link #nonIndexedValues()}, each in the order in which the parameters were
 * declared.
 *
 * <p><strong>Note on indexed dynamic types:</strong> The EVM does not store the value of an indexed
 * dynamic type (for example {@code string} or {@code bytes}) in the log. Only the {@code keccak256}
 * hash of the value is available in {@link #topics()}. For such parameters, the corresponding entry
 * in {@link #indexedValues()} therefore represents that hash and not the original value.
 *
 * @param address the address of the contract that emitted the event
 * @param topics the topics of the log. The first topic is the hash of the event signature, the
 *     following topics contain the indexed parameters
 * @param blockHash the hash of the block that contains the log
 * @param logIndex the index of the log in the block
 * @param blockNumber the number of the block that contains the log
 * @param transactionHash the hash of the transaction that emitted the log
 * @param transactionIndex the index of the transaction in the block
 * @param data the raw (ABI encoded) data of the log that contains the non-indexed parameters
 * @param indexedValues the decoded values of the indexed event parameters
 * @param nonIndexedValues the decoded values of the non-indexed event parameters
 */
public record EventLog(
    @NonNull String address,
    @NonNull List<String> topics,
    @NonNull String blockHash,
    @NonNull BigInteger logIndex,
    @NonNull BigInteger blockNumber,
    @NonNull String transactionHash,
    @NonNull BigInteger transactionIndex,
    @NonNull String data,
    @NonNull List<Type> indexedValues,
    @NonNull List<Type> nonIndexedValues) {

  public EventLog {
    Objects.requireNonNull(address, "address must not be null");
    Objects.requireNonNull(topics, "topics must not be null");
    Objects.requireNonNull(blockHash, "blockHash must not be null");
    Objects.requireNonNull(logIndex, "logIndex must not be null");
    Objects.requireNonNull(blockNumber, "blockNumber must not be null");
    Objects.requireNonNull(transactionHash, "transactionHash must not be null");
    Objects.requireNonNull(transactionIndex, "transactionIndex must not be null");
    Objects.requireNonNull(data, "data must not be null");
    Objects.requireNonNull(indexedValues, "indexedValues must not be null");
    Objects.requireNonNull(nonIndexedValues, "nonIndexedValues must not be null");
    topics = List.copyOf(topics);
    indexedValues = List.copyOf(indexedValues);
    nonIndexedValues = List.copyOf(nonIndexedValues);
  }
}
