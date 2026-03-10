package com.openelements.hiero.smartcontract.abi.model;

import org.jspecify.annotations.NonNull;

/** Represents the possible state mutability of a smart contract function. */
public enum StateMutability {
  PURE,
  VIEW,
  PAYABLE,
  NON_PAYABLE;

  /**
   * Returns the state mutability of a smart contract function based on its name.
   *
   * @param name the name of the state mutability
   * @return the corresponding state mutability
   */
  @NonNull
  public static StateMutability of(@NonNull final String name) {
    return switch (name) {
      case "pure" -> PURE;
      case "view" -> VIEW;
      case "payable" -> PAYABLE;
      case "nonpayable" -> NON_PAYABLE;
      default -> throw new IllegalArgumentException("Unknown state mutability: " + name);
    };
  }
}
