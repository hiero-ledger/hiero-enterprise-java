package org.hiero.base.event;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.AbiTypes;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;

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
   * Creates a param from any Web3j ABI type class.
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
   * @param solidityType solidity type name, e.g. {@code "uint128"}, {@code "bytes4"}
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

  public static @NonNull EventParam uintN(int bits, boolean indexed) {
    return of("uint" + requireBits(bits), indexed);
  }

  // Int

  public static @NonNull EventParam intN(int bits, boolean indexed) {
    return of("int" + requireBits(bits), indexed);
  }

  // Bytes

  public static @NonNull EventParam bytesN(int size, boolean indexed) {
    if (size < 1 || size > 32) {
      throw new IllegalArgumentException("bytesN size must be in [1, 32], got " + size);
    }
    return of("bytes" + size, indexed);
  }

  public static @NonNull EventParam bytes(boolean indexed) {
    return of(DynamicBytes.class, indexed);
  }

  // Bool

  public static @NonNull EventParam bool(boolean indexed) {
    return of(Bool.class, indexed);
  }

  // Address

  public static @NonNull EventParam address(boolean indexed) {
    return of(Address.class, indexed);
  }

  private static int requireBits(int bits) {
    if ((bits < 8 || bits > 256) && bits % 8 != 0) {
      throw new IllegalArgumentException(
          "bit size must be a multiple of 8 of [8, 256], got " + bits);
    }

    return bits;
  }
}
