package org.hiero.base.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.hiero.base.data.EventLog;
import org.hiero.base.implementation.EventLogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.Log;

public class EventLogTest {

  private static final String CONTRACT_ADDRESS = "0x" + "ab".repeat(20);
  private static final String SIGNATURE_TOPIC = "0x" + "11".repeat(32);
  private static final String INDEXED_TOPIC = "0x" + "00".repeat(12) + "cd".repeat(20);
  private static final String BLOCK_HASH = "0x" + "22".repeat(32);
  private static final String TRANSACTION_HASH = "0x" + "33".repeat(32);
  private static final String DATA = "0x" + "00".repeat(31) + "2a";

  private static Log createLog() {
    return new Log(
        false,
        "0x3", // logIndex
        "0x1", // transactionIndex
        TRANSACTION_HASH,
        BLOCK_HASH,
        "0x10", // blockNumber
        CONTRACT_ADDRESS,
        DATA,
        "mined",
        List.of(SIGNATURE_TOPIC, INDEXED_TOPIC));
  }

  @Test
  void testRecordHoldsAllFields() {
    // given
    final List<Type> indexed = List.of(new Address("0x" + "cd".repeat(20)));
    final List<Type> nonIndexed = List.of(new Uint256(BigInteger.valueOf(42)));

    // when
    final EventLog eventLog =
        new EventLog(
            CONTRACT_ADDRESS,
            List.of(SIGNATURE_TOPIC, INDEXED_TOPIC),
            BLOCK_HASH,
            BigInteger.valueOf(3),
            BigInteger.valueOf(16),
            TRANSACTION_HASH,
            BigInteger.ONE,
            DATA,
            indexed,
            nonIndexed);

    // then
    Assertions.assertEquals(CONTRACT_ADDRESS, eventLog.address());
    Assertions.assertEquals(List.of(SIGNATURE_TOPIC, INDEXED_TOPIC), eventLog.topics());
    Assertions.assertEquals(BLOCK_HASH, eventLog.blockHash());
    Assertions.assertEquals(BigInteger.valueOf(3), eventLog.logIndex());
    Assertions.assertEquals(BigInteger.valueOf(16), eventLog.blockNumber());
    Assertions.assertEquals(TRANSACTION_HASH, eventLog.transactionHash());
    Assertions.assertEquals(BigInteger.ONE, eventLog.transactionIndex());
    Assertions.assertEquals(DATA, eventLog.data());
    Assertions.assertEquals(indexed, eventLog.indexedValues());
    Assertions.assertEquals(nonIndexed, eventLog.nonIndexedValues());
  }

  @Test
  void testFactoryMapsAllFieldsFromWeb3jLog() {
    // given
    final List<Type> indexed = List.of(new Address("0x" + "cd".repeat(20)));
    final List<Type> nonIndexed =
        List.of(new Uint256(BigInteger.valueOf(42)), new Utf8String("hi"));

    // when
    final EventLog eventLog = EventLogFactory.create(createLog(), indexed, nonIndexed);

    // then
    Assertions.assertEquals(CONTRACT_ADDRESS, eventLog.address());
    Assertions.assertEquals(List.of(SIGNATURE_TOPIC, INDEXED_TOPIC), eventLog.topics());
    Assertions.assertEquals(BLOCK_HASH, eventLog.blockHash());
    Assertions.assertEquals(BigInteger.valueOf(3), eventLog.logIndex());
    Assertions.assertEquals(BigInteger.valueOf(16), eventLog.blockNumber());
    Assertions.assertEquals(TRANSACTION_HASH, eventLog.transactionHash());
    Assertions.assertEquals(BigInteger.ONE, eventLog.transactionIndex());
    Assertions.assertEquals(DATA, eventLog.data());
    Assertions.assertEquals(indexed, eventLog.indexedValues());
    Assertions.assertEquals(nonIndexed, eventLog.nonIndexedValues());
  }

  @Test
  void testEmptyValueListsAreAllowed() {
    // when
    final EventLog eventLog = EventLogFactory.create(createLog(), List.of(), List.of());

    // then
    Assertions.assertTrue(eventLog.indexedValues().isEmpty());
    Assertions.assertTrue(eventLog.nonIndexedValues().isEmpty());
  }

  @Test
  void testListsAreDefensivelyCopied() {
    // given
    final List<String> topics = new ArrayList<>(List.of(SIGNATURE_TOPIC));
    final List<Type> indexed = new ArrayList<>();
    final List<Type> nonIndexed = new ArrayList<>(List.of(new Uint256(BigInteger.ONE)));

    final EventLog eventLog =
        new EventLog(
            CONTRACT_ADDRESS,
            topics,
            BLOCK_HASH,
            BigInteger.ZERO,
            BigInteger.ZERO,
            TRANSACTION_HASH,
            BigInteger.ZERO,
            DATA,
            indexed,
            nonIndexed);

    // when
    topics.add(INDEXED_TOPIC);
    indexed.add(new Uint256(BigInteger.TEN));
    nonIndexed.clear();

    // then
    Assertions.assertEquals(List.of(SIGNATURE_TOPIC), eventLog.topics());
    Assertions.assertTrue(eventLog.indexedValues().isEmpty());
    Assertions.assertEquals(1, eventLog.nonIndexedValues().size());
  }

  @Test
  void testExposedListsAreImmutable() {
    // given
    final EventLog eventLog =
        EventLogFactory.create(createLog(), List.of(), List.of(new Uint256(BigInteger.ONE)));

    // then
    Assertions.assertThrows(UnsupportedOperationException.class, () -> eventLog.topics().clear());
    Assertions.assertThrows(
        UnsupportedOperationException.class, () -> eventLog.indexedValues().clear());
    Assertions.assertThrows(
        UnsupportedOperationException.class, () -> eventLog.nonIndexedValues().clear());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
  void testNullValuesAreRejected(final int indexOfNullArgument) {
    // given
    final Object[] arguments = {
      CONTRACT_ADDRESS,
      List.of(SIGNATURE_TOPIC),
      BLOCK_HASH,
      BigInteger.ZERO,
      BigInteger.ZERO,
      TRANSACTION_HASH,
      BigInteger.ZERO,
      DATA,
      List.<Type>of(),
      List.<Type>of()
    };
    arguments[indexOfNullArgument] = null;

    // then
    Assertions.assertThrows(NullPointerException.class, () -> createEventLog(arguments));
  }

  @SuppressWarnings("unchecked")
  private static EventLog createEventLog(final Object[] a) {
    return new EventLog(
        (String) a[0],
        (List<String>) a[1],
        (String) a[2],
        (BigInteger) a[3],
        (BigInteger) a[4],
        (String) a[5],
        (BigInteger) a[6],
        (String) a[7],
        (List<Type>) a[8],
        (List<Type>) a[9]);
  }

  @Test
  void testFactoryRejectsNullLog() {
    Assertions.assertThrows(
        NullPointerException.class, () -> EventLogFactory.create(null, List.of(), List.of()));
  }
}
