package org.hiero.base.test;

import java.util.stream.Stream;
import org.hiero.base.event.EventParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes10;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.abi.datatypes.generated.Bytes25;
import org.web3j.abi.datatypes.generated.Bytes3;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Int16;
import org.web3j.abi.datatypes.generated.Int224;
import org.web3j.abi.datatypes.generated.Int232;
import org.web3j.abi.datatypes.generated.Int24;
import org.web3j.abi.datatypes.generated.Int240;
import org.web3j.abi.datatypes.generated.Int248;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Int32;
import org.web3j.abi.datatypes.generated.Int40;
import org.web3j.abi.datatypes.generated.Int8;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint224;
import org.web3j.abi.datatypes.generated.Uint232;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.abi.datatypes.generated.Uint240;
import org.web3j.abi.datatypes.generated.Uint248;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint40;
import org.web3j.abi.datatypes.generated.Uint8;

public class EventParamTest {
  static Stream<Arguments> typesAndFlags() {
    return Stream.of(
            Address.class,
            Bool.class,
            Utf8String.class,
            DynamicBytes.class,
            Bytes32.class,
            Uint256.class,
            Int256.class)
        .flatMap(type -> Stream.of(true, false).map(indexed -> Arguments.of(type, indexed)));
  }

  @ParameterizedTest(name = "of({0}, indexed={1})")
  @MethodSource("typesAndFlags")
  void testEventParamCanBeCreatedUsingWeb3jType(Class<? extends Type<?>> type, boolean indexed) {
    final EventParam result = EventParam.of(type, indexed);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(type, result.typeReference().getType());
    Assertions.assertEquals(indexed, result.isIndexed());
  }

  static Stream<Arguments> solidityTypes() {
    return Stream.of(
        Arguments.of("address", Address.class),
        Arguments.of("bool", Bool.class),
        Arguments.of("uint128", Uint128.class),
        Arguments.of("int256", Int256.class),
        Arguments.of("bytes4", Bytes4.class));
  }

  @ParameterizedTest(name = "of({0}, indexed={1})")
  @MethodSource("solidityTypes")
  void testEventParamCanBeCreatedFromSolidityType(
      String solidityType, Class<? extends Type> clazz) {
    final EventParam result = EventParam.of(solidityType, false);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(clazz, result.typeReference().getType());
  }

  @Test
  void testInvalidSolidityTypeRaiseError() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> EventParam.of("invalid", false));
  }

  static Stream<Arguments> uintTypes() {
    return Stream.of(
        Arguments.of(8, Uint8.class),
        Arguments.of(16, Uint16.class),
        Arguments.of(24, Uint24.class),
        Arguments.of(32, Uint32.class),
        Arguments.of(40, Uint40.class),
        Arguments.of(224, Uint224.class),
        Arguments.of(232, Uint232.class),
        Arguments.of(240, Uint240.class),
        Arguments.of(248, Uint248.class),
        Arguments.of(256, Uint256.class));
  }

  @ParameterizedTest(name = "uint{0}")
  @MethodSource("uintTypes")
  void testEventParamUsingUintN(int bits, Class<? extends Type> clazz) {
    final EventParam result = EventParam.uintN(bits, false);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(clazz, result.typeReference().getType());
  }

  @ParameterizedTest(name = "uint(bits = {0})")
  @ValueSource(ints = {-1, 0, 6, 55, 257})
  void testInvalidUintRangeThrowsError(int bits) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> EventParam.uintN(bits, false));
  }

  static Stream<Arguments> intTypes() {
    return Stream.of(
        Arguments.of(8, Int8.class),
        Arguments.of(16, Int16.class),
        Arguments.of(24, Int24.class),
        Arguments.of(32, Int32.class),
        Arguments.of(40, Int40.class),
        Arguments.of(224, Int224.class),
        Arguments.of(232, Int232.class),
        Arguments.of(240, Int240.class),
        Arguments.of(248, Int248.class),
        Arguments.of(256, Int256.class));
  }

  @ParameterizedTest(name = "int{0}")
  @MethodSource("intTypes")
  void testEventParamUsingIntN(int bits, Class<? extends Type> clazz) {
    final EventParam result = EventParam.intN(bits, false);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(clazz, result.typeReference().getType());
  }

  @ParameterizedTest(name = "int(bits = {0})")
  @ValueSource(ints = {-1, 0, 6, 55, 257})
  void testInvalidIntRangeThrowsError(int bits) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> EventParam.intN(bits, false));
  }

  static Stream<Arguments> bytesTypes() {
    return Stream.of(
        Arguments.of(1, Bytes1.class),
        Arguments.of(2, Bytes2.class),
        Arguments.of(3, Bytes3.class),
        Arguments.of(10, Bytes10.class),
        Arguments.of(25, Bytes25.class),
        Arguments.of(32, Bytes32.class));
  }

  @ParameterizedTest(name = "bytes{0}")
  @MethodSource("bytesTypes")
  void testEventParamUsingBytesN(int size, Class<? extends Type> clazz) {
    final EventParam result = EventParam.bytesN(size, false);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(clazz, result.typeReference().getType());
  }

  @ParameterizedTest(name = "bytes(size = {0})")
  @ValueSource(ints = {-1, 0, 33, 257})
  void testInvalidByteSizeThrowsError(int size) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> EventParam.bytesN(size, false));
  }

  @ParameterizedTest(name = "bytes(indexed = {0})")
  @ValueSource(booleans = {true, false})
  void testDynamicBytesEventParams(boolean indexed) {
    final EventParam result = EventParam.bytes(indexed);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(DynamicBytes.class, result.typeReference().getType());
    Assertions.assertEquals(indexed, result.isIndexed());
  }

  @ParameterizedTest(name = "address(indexed = {0})")
  @ValueSource(booleans = {true, false})
  void testAddressEventParams(boolean indexed) {
    final EventParam result = EventParam.address(indexed);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(Address.class, result.typeReference().getType());
    Assertions.assertEquals(indexed, result.isIndexed());
  }

  @ParameterizedTest(name = "bool(indexed = {0})")
  @ValueSource(booleans = {true, false})
  void testBoolEventParams(boolean indexed) {
    final EventParam result = EventParam.bool(indexed);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(Bool.class, result.typeReference().getType());
    Assertions.assertEquals(indexed, result.isIndexed());
  }
}
