package com.openelements.hiero.smartcontract.abi.model;

import org.jspecify.annotations.NonNull;

/** Represents the possible types of an ABI parameter (see {@link AbiParameter}). */
public enum AbiParameterType {
  ADDRESS,
  STRING,
  BYTE32,
  BOOL,
  UINT256,
  UINT,
  TUPLE;

  /**
   * Returns the ABI parameter type corresponding to the given name.
   *
   * @param name the name of the ABI parameter type
   * @return the corresponding ABI parameter type
   */
  @NonNull
  public static AbiParameterType of(@NonNull final String name) {
    return switch (name) {
      case "address" -> ADDRESS;
      case "string" -> STRING;
      case "bytes32" -> BYTE32;
      case "bool" -> BOOL;
      case "uint256" -> UINT256;
      case "uint" -> UINT;
      case "tuple" -> TUPLE;
      default -> throw new IllegalArgumentException("Unknown value type: " + name);
    };
  }

  /**
   * Returns the canonical type of the ABI parameter.
   *
   * @return the canonical type of the ABI parameter
   */
  @NonNull
  public String getCanonicalType() {
    return switch (this) {
      case ADDRESS -> "address";
      case STRING -> "string";
      case BYTE32 -> "bytes32";
      case BOOL -> "bool";
      case UINT256 -> "uint256";
      case TUPLE -> "tuple";
      case UINT -> "uint256";
    };
  }

  /**
   * Checks if the ABI parameter type is dynamic.
   *
   * @return true if the ABI parameter type is dynamic, false otherwise
   */
  public boolean isDynamic() {
    return switch (this) {
      case STRING, BYTE32 -> true;
      default -> false;
    };
  }

  /**
   * Returns the fixed size of the ABI parameter type in bytes or throws an {@link
   * IllegalStateException} if the type is dynamic.
   *
   * @return the fixed size of the ABI parameter type in bytes
   */
  public int getFixedSize() {
    return switch (this) {
      case ADDRESS -> 20;
      case BOOL -> 1;
      case UINT256 -> 32;
      case UINT -> 32;
      case BYTE32 -> 32;
      default -> throw new IllegalStateException("No fixed size for type: " + this);
    };
  }
}
