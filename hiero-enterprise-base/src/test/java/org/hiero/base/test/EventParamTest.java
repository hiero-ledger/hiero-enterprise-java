package org.hiero.base.test;

import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;
import org.hiero.base.event.EventParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.web3j.abi.TypeReference;
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

  private static void assertDynamicArray(EventParam param, Class<? extends Type> clazz)
      throws ClassNotFoundException {
    Assertions.assertEquals(param.typeReference().getClassType().getSimpleName(), "DynamicArray");

    final java.lang.reflect.Type refType = param.typeReference().getType();
    Assertions.assertTrue(refType instanceof ParameterizedType);

    final ParameterizedType paramType = (ParameterizedType) refType;
    final java.lang.reflect.Type[] actualTypeArguments = paramType.getActualTypeArguments();

    // Assert there is exactly one argument
    Assertions.assertEquals(1, actualTypeArguments.length);
    Assertions.assertEquals(clazz, actualTypeArguments[0]);
  }

  static Stream<Arguments> dynamicArrayTypes() {
    return Stream.of(
        Arguments.of("uint256[]", Uint256.class),
        Arguments.of("int256[]", Int256.class),
        Arguments.of("string[]", Utf8String.class),
        Arguments.of("address[]", Address.class),
        Arguments.of("bool[]", Bool.class));
  }

  @ParameterizedTest(name = "array(type = {0})")
  @MethodSource("dynamicArrayTypes")
  void testArrayOfForDynamicTypes(String type, Class<? extends Type> clazz)
      throws ClassNotFoundException {
    final EventParam result = EventParam.arrayOf(type, false);
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isIndexed());
    assertDynamicArray(result, clazz);
  }

  static Stream<Arguments> dynamicArray() {
    return Stream.of(
        Arguments.of(EventParam.uint256(false), Uint256.class),
        Arguments.of(EventParam.int16(false), Int16.class),
        Arguments.of(EventParam.string(false), Utf8String.class),
        Arguments.of(EventParam.address(false), Address.class),
        Arguments.of(EventParam.bool(false), Bool.class));
  }

  @ParameterizedTest(name = "array(type = {0})")
  @MethodSource("dynamicArray")
  void testCreateDynamicArray(EventParam eventParam, Class<? extends Type> clazz)
      throws ClassNotFoundException {
    final EventParam result = EventParam.array(eventParam, false);
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isIndexed());
    assertDynamicArray(result, clazz);
  }

  private static void assertFixedArray(EventParam param, int length, Class<? extends Type> clazz)
      throws ClassNotFoundException {
    String typeClass = "StaticArray";
    if (length <= 32) {
      typeClass += length;
    }

    Assertions.assertTrue(param.typeReference() instanceof TypeReference.StaticArrayTypeReference);

    final TypeReference.StaticArrayTypeReference<?> staticRef =
        (TypeReference.StaticArrayTypeReference<?>) param.typeReference();
    Assertions.assertEquals(length, staticRef.getSize());
    Assertions.assertEquals(typeClass, param.typeReference().getClassType().getSimpleName());

    final java.lang.reflect.Type refType = param.typeReference().getType();
    Assertions.assertTrue(refType instanceof ParameterizedType);

    final ParameterizedType paramType = (ParameterizedType) refType;
    final java.lang.reflect.Type[] actualTypeArguments = paramType.getActualTypeArguments();
    Assertions.assertEquals(clazz, actualTypeArguments[0]);
  }

  static Stream<Arguments> fixedArrayTypes() {
    return Stream.of(
        Arguments.of("uint256[10]", 10, Uint256.class),
        Arguments.of("int256[1]", 1, Int256.class),
        Arguments.of("string[4]", 4, Utf8String.class),
        Arguments.of("address[32]", 32, Address.class),
        Arguments.of("bool[30]", 30, Bool.class));
  }

  @ParameterizedTest(name = "array(type = {0})")
  @MethodSource("fixedArrayTypes")
  void testArrayOfForFixedTypes(String type, int length, Class<? extends Type> clazz)
      throws ClassNotFoundException {
    final EventParam result = EventParam.arrayOf(type, false);
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isIndexed());
    assertFixedArray(result, length, clazz);
  }

  static Stream<Arguments> fixedArray() {
    return Stream.of(
        Arguments.of(EventParam.uint256(false), 10, Uint256.class),
        Arguments.of(EventParam.int16(false), 1, Int16.class),
        Arguments.of(EventParam.string(false), 32, Utf8String.class),
        Arguments.of(EventParam.address(false), 12, Address.class),
        Arguments.of(EventParam.bool(false), 3, Bool.class));
  }

  @ParameterizedTest(name = "array(type = {0})")
  @MethodSource("fixedArray")
  void testCreateFixedArray(EventParam eventParam, int length, Class<? extends Type> clazz)
      throws ClassNotFoundException {
    final EventParam result = EventParam.array(eventParam, length, false);
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isIndexed());
    assertFixedArray(result, length, clazz);
  }

  @Test
  void testArrayOfForFixedTypesLengthGreaterThan32() throws ClassNotFoundException {
    final EventParam result = EventParam.arrayOf("uint256[33]", false);
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isIndexed());
    assertFixedArray(result, 33, Uint256.class);
  }

  @ParameterizedTest(name = "array(length = {0})")
  @ValueSource(ints = {-1, 0})
  void testCreateFixedArrayForInvalidLength(int length) {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> EventParam.array(EventParam.int24(false), length, false));
  }

  @ParameterizedTest(name = "address(type = {0})")
  @ValueSource(strings = {"invalid[]", "uint256[-1]", "uint256{}", "string()"})
  void testArrayOfForInvalidType(String type) {
    Assertions.assertThrows(IllegalArgumentException.class, () -> EventParam.arrayOf(type, false));
  }
}
