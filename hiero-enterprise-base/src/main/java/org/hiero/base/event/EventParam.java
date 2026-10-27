package org.hiero.base.event;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.AbiTypes;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;

/**
 * Wrapper for Web3j {@link TypeReference} for create smart contract event params.
 *
 * @param typeReference the {@link TypeReference} representing the event params (ABI types, and
 *     index flag)
 */
public record EventParam(@NonNull TypeReference<?> typeReference) {
  public EventParam {
    Objects.requireNonNull(typeReference, "typeReference must not be null");
  }

  /**
   * Check whether param declared is indexed.
   *
   * @return {@code true} if this param is declared {@code indexed} in the event
   */
  public boolean isIndexed() {
    return typeReference.isIndexed();
  }

  /**
   * Creates a param from any Web3j ABI type class. d
   *
   * @param type web3j ABI type class
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam of(@NonNull Class<? extends Type> type, boolean indexed) {
    Objects.requireNonNull(type, "type must not be null");
    return new EventParam(TypeReference.create(type, indexed));
  }

  /**
   * Creates a param from a solidity type name.
   *
   * @param solidityType solidity type name, e.g. {@code "uuint128"}, {@code "bytes4"}
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   * @throws IllegalArgumentException if the type is not valid solidity type
   */
  public static @NonNull EventParam of(@NonNull String solidityType, boolean indexed) {
    Objects.requireNonNull(solidityType, "solidityType must not be null");
    try {
      return of(AbiTypes.getType(solidityType), indexed);
    } catch (UnsupportedOperationException e) {
      throw new IllegalArgumentException("Invalid Solidity type: " + solidityType, e);
    }
  }

  // Uint

  /**
   * Create an uint param of n size.
   *
   * @param bits the size of the uint
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   * @throws IllegalArgumentException if the bits are not valid.
   */
  public static @NonNull EventParam uintN(int bits, boolean indexed) {
    return of("uint" + requireBits(bits), indexed);
  }

  /**
   * Create an uint8 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint8(boolean indexed) {
    return uintN(8, indexed);
  }

  /**
   * Create an uint16 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint16(boolean indexed) {
    return uintN(16, indexed);
  }

  /**
   * Create an uint24 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint24(boolean indexed) {
    return uintN(24, indexed);
  }

  /**
   * Create an uint32 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint32(boolean indexed) {
    return uintN(32, indexed);
  }

  /**
   * Create an uint40 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint40(boolean indexed) {
    return uintN(40, indexed);
  }

  /**
   * Create an uint48 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint48(boolean indexed) {
    return uintN(48, indexed);
  }

  /**
   * Create an uint56 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint56(boolean indexed) {
    return uintN(56, indexed);
  }

  /**
   * Create an uint64 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint64(boolean indexed) {
    return uintN(64, indexed);
  }

  /**
   * Create an uint72 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint72(boolean indexed) {
    return uintN(72, indexed);
  }

  /**
   * Create an uint80 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint80(boolean indexed) {
    return uintN(80, indexed);
  }

  /**
   * Create an uint88 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint88(boolean indexed) {
    return uintN(88, indexed);
  }

  /**
   * Create an uint96 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint96(boolean indexed) {
    return uintN(96, indexed);
  }

  /**
   * Create an uint104 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint104(boolean indexed) {
    return uintN(104, indexed);
  }

  /**
   * Create an uint112 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint112(boolean indexed) {
    return uintN(112, indexed);
  }

  /**
   * Create an uint120 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint120(boolean indexed) {
    return uintN(120, indexed);
  }

  /**
   * Create an uint128 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint128(boolean indexed) {
    return uintN(128, indexed);
  }

  /**
   * Create an uint136 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint136(boolean indexed) {
    return uintN(136, indexed);
  }

  /**
   * Create an uint144 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint144(boolean indexed) {
    return uintN(144, indexed);
  }

  /**
   * Create an uint152 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint152(boolean indexed) {
    return uintN(152, indexed);
  }

  /**
   * Create an uint160 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint160(boolean indexed) {
    return uintN(160, indexed);
  }

  /**
   * Create an uint168 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint168(boolean indexed) {
    return uintN(168, indexed);
  }

  /**
   * Create an uint176 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint176(boolean indexed) {
    return uintN(176, indexed);
  }

  /**
   * Create an uint184 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint184(boolean indexed) {
    return uintN(184, indexed);
  }

  /**
   * Create an uint192 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint192(boolean indexed) {
    return uintN(192, indexed);
  }

  /**
   * Create an uint200 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint200(boolean indexed) {
    return uintN(200, indexed);
  }

  /**
   * Create an uint208 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint208(boolean indexed) {
    return uintN(208, indexed);
  }

  /**
   * Create an uint216 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint216(boolean indexed) {
    return uintN(216, indexed);
  }

  /**
   * Create an uint224 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint224(boolean indexed) {
    return uintN(224, indexed);
  }

  /**
   * Create an uint232 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint232(boolean indexed) {
    return uintN(232, indexed);
  }

  /**
   * Create an uint240 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint240(boolean indexed) {
    return uintN(240, indexed);
  }

  /**
   * Create an uint248 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint248(boolean indexed) {
    return uintN(248, indexed);
  }

  /**
   * Create an uint256 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam uint256(boolean indexed) {
    return uintN(256, indexed);
  }

  // Int

  /**
   * Create an int param of n size.
   *
   * @param bits the size of the int
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   * @throws IllegalArgumentException if the bits are not valid.
   */
  public static @NonNull EventParam intN(int bits, boolean indexed) {
    return of("int" + requireBits(bits), indexed);
  }

  /**
   * Create an int8 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int8(boolean indexed) {
    return intN(8, indexed);
  }

  /**
   * Create an int16 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int16(boolean indexed) {
    return intN(16, indexed);
  }

  /**
   * Create an int24 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int24(boolean indexed) {
    return intN(24, indexed);
  }

  /**
   * Create an int32 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int32(boolean indexed) {
    return intN(32, indexed);
  }

  /**
   * Create an int40 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int40(boolean indexed) {
    return intN(40, indexed);
  }

  /**
   * Create an int48 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int48(boolean indexed) {
    return intN(48, indexed);
  }

  /**
   * Create an int56 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int56(boolean indexed) {
    return intN(56, indexed);
  }

  /**
   * Create an int64 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int64(boolean indexed) {
    return intN(64, indexed);
  }

  /**
   * Create an int72 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int72(boolean indexed) {
    return intN(72, indexed);
  }

  /**
   * Create an int80 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int80(boolean indexed) {
    return intN(80, indexed);
  }

  /**
   * Create an int88 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int88(boolean indexed) {
    return intN(88, indexed);
  }

  /**
   * Create an int96 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int96(boolean indexed) {
    return intN(96, indexed);
  }

  /**
   * Create an int104 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int104(boolean indexed) {
    return intN(104, indexed);
  }

  /**
   * Create an int112 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int112(boolean indexed) {
    return intN(112, indexed);
  }

  /**
   * Create an int120 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int120(boolean indexed) {
    return intN(120, indexed);
  }

  /**
   * Create an int128 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int128(boolean indexed) {
    return intN(128, indexed);
  }

  /**
   * Create an int136 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int136(boolean indexed) {
    return intN(136, indexed);
  }

  /**
   * Create an int144 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int144(boolean indexed) {
    return intN(144, indexed);
  }

  /**
   * Create an int152 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int152(boolean indexed) {
    return intN(152, indexed);
  }

  /**
   * Create an int160 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int160(boolean indexed) {
    return intN(160, indexed);
  }

  /**
   * Create an int168 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int168(boolean indexed) {
    return intN(168, indexed);
  }

  /**
   * Create an int176 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int176(boolean indexed) {
    return intN(176, indexed);
  }

  /**
   * Create an int184 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int184(boolean indexed) {
    return intN(184, indexed);
  }

  /**
   * Create an int192 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int192(boolean indexed) {
    return intN(192, indexed);
  }

  /**
   * Create an int200 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int200(boolean indexed) {
    return intN(200, indexed);
  }

  /**
   * Create an int208 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int208(boolean indexed) {
    return intN(208, indexed);
  }

  /**
   * Create an int216 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int216(boolean indexed) {
    return intN(216, indexed);
  }

  /**
   * Create an int224 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int224(boolean indexed) {
    return intN(224, indexed);
  }

  /**
   * Create an int232 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int232(boolean indexed) {
    return intN(232, indexed);
  }

  /**
   * Create an int240 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int240(boolean indexed) {
    return intN(240, indexed);
  }

  /**
   * Create an int248 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int248(boolean indexed) {
    return intN(248, indexed);
  }

  /**
   * Create an int256 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam int256(boolean indexed) {
    return intN(256, indexed);
  }

  // Bytes

  /**
   * Create a bytes param of n size.
   *
   * @param size size for the bytes.
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   * @throws IllegalArgumentException if size is not in range 1 to 31
   */
  public static @NonNull EventParam bytesN(int size, boolean indexed) {
    if (size < 1 || size > 32) {
      throw new IllegalArgumentException("bytesN size must be in [1, 32], got " + size);
    }
    return of("bytes" + size, indexed);
  }

  /**
   * Create a dynamic bytes param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes(boolean indexed) {
    return of(DynamicBytes.class, indexed);
  }

  /**
   * Create a bytes1 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes1(boolean indexed) {
    return bytesN(1, indexed);
  }

  /**
   * Create a bytes2 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes2(boolean indexed) {
    return bytesN(2, indexed);
  }

  /**
   * Create a bytes3 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes3(boolean indexed) {
    return bytesN(3, indexed);
  }

  /**
   * Create a bytes4 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes4(boolean indexed) {
    return bytesN(4, indexed);
  }

  /**
   * Create a bytes5 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes5(boolean indexed) {
    return bytesN(5, indexed);
  }

  /**
   * Create a bytes6 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes6(boolean indexed) {
    return bytesN(6, indexed);
  }

  /**
   * Create a bytes7 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes7(boolean indexed) {
    return bytesN(7, indexed);
  }

  /**
   * Create a bytes8 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes8(boolean indexed) {
    return bytesN(8, indexed);
  }

  /**
   * Create a bytes9 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes9(boolean indexed) {
    return bytesN(9, indexed);
  }

  /**
   * Create a bytes10 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes10(boolean indexed) {
    return bytesN(10, indexed);
  }

  /**
   * Create a bytes11 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes11(boolean indexed) {
    return bytesN(11, indexed);
  }

  /**
   * Create a bytes12 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes12(boolean indexed) {
    return bytesN(12, indexed);
  }

  /**
   * Create a bytes13 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes13(boolean indexed) {
    return bytesN(13, indexed);
  }

  /**
   * Create a bytes14 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes14(boolean indexed) {
    return bytesN(14, indexed);
  }

  /**
   * Create a bytes15 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes15(boolean indexed) {
    return bytesN(15, indexed);
  }

  /**
   * Create a bytes16 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes16(boolean indexed) {
    return bytesN(16, indexed);
  }

  /**
   * Create a bytes17 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes17(boolean indexed) {
    return bytesN(17, indexed);
  }

  /**
   * Create a bytes18 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes18(boolean indexed) {
    return bytesN(18, indexed);
  }

  /**
   * Create a bytes19 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes19(boolean indexed) {
    return bytesN(19, indexed);
  }

  /**
   * Create a bytes20 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes20(boolean indexed) {
    return bytesN(20, indexed);
  }

  /**
   * Create a bytes21 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes21(boolean indexed) {
    return bytesN(21, indexed);
  }

  /**
   * Create a bytes22 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes22(boolean indexed) {
    return bytesN(22, indexed);
  }

  /**
   * Create a bytes23 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes23(boolean indexed) {
    return bytesN(23, indexed);
  }

  /**
   * Create a bytes24 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes24(boolean indexed) {
    return bytesN(24, indexed);
  }

  /**
   * Create a bytes25 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes25(boolean indexed) {
    return bytesN(25, indexed);
  }

  /**
   * Create a bytes26 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes26(boolean indexed) {
    return bytesN(26, indexed);
  }

  /**
   * Create a bytes27 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes27(boolean indexed) {
    return bytesN(27, indexed);
  }

  /**
   * Create a bytes28 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes28(boolean indexed) {
    return bytesN(28, indexed);
  }

  /**
   * Create a bytes29 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes29(boolean indexed) {
    return bytesN(29, indexed);
  }

  /**
   * Create a bytes30 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes30(boolean indexed) {
    return bytesN(30, indexed);
  }

  /**
   * Create a bytes31 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes31(boolean indexed) {
    return bytesN(31, indexed);
  }

  /**
   * Create a bytes32 param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bytes32(boolean indexed) {
    return bytesN(32, indexed);
  }

  // Bool

  /**
   * Create a bool param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam bool(boolean indexed) {
    return of(Bool.class, indexed);
  }

  // Address

  /**
   * Create an address param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam address(boolean indexed) {
    return of(Address.class, indexed);
  }

  // String

  /**
   * Creates a string param.
   *
   * @param indexed state whether the type is indexed
   * @return new instance of {@link EventParam}
   */
  public static @NonNull EventParam string(boolean indexed) {
    return of(Utf8String.class, indexed);
  }

  private static int requireBits(int bits) {
    if ((bits < 8 || bits > 256) && bits % 8 != 0) {
      throw new IllegalArgumentException(
          "bit size must be a multiple of 8 of [8, 256], got " + bits);
    }

    return bits;
  }
}
