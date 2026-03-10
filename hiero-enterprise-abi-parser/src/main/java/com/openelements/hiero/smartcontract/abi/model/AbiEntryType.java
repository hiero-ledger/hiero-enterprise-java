package com.openelements.hiero.smartcontract.abi.model;

import java.util.stream.Stream;

/** Represents the possible types of an ABI entry (see {@link AbiEntry}). */
public enum AbiEntryType {
  FUNCTION,
  ERROR,
  EVENT,
  CONSTRUCTOR,
  RECEIVE,
  FALLBACK;

  /**
   * Returns the ABI entry type corresponding to the given name.
   *
   * @param name the name of the ABI entry type
   * @return the corresponding ABI entry type
   */
  public static AbiEntryType of(String name) {
    return switch (name) {
      case "function" -> FUNCTION;
      case "event" -> EVENT;
      case "error" -> ERROR;
      case "constructor" -> CONSTRUCTOR;
      case "receive" -> RECEIVE;
      case "fallback" -> FALLBACK;
      default -> throw new IllegalArgumentException("Unknown ABI entry type: " + name);
    };
  }

  /**
   * Checks if the ABI entry type is compatible with a function (as defined by {@link AbiFunction}).
   *
   * @return true if the ABI entry type is compatible with a function, false otherwise
   */
  public boolean isCompatibleWithFunction() {
    return Stream.of(FUNCTION, CONSTRUCTOR, RECEIVE, FALLBACK).anyMatch(type -> this == type);
  }
}
