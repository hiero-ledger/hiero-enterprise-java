package com.openelements.hiero.smartcontract.abi.model;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a function in the ABI (Application Binary Interface) of a smart contract.
 *
 * @param type the type of the function
 * @param name the name of the function
 * @param inputs the input parameters of the function
 * @param outputs the output parameters of the function
 * @param stateMutability the state mutability of the function
 */
public record AbiFunction(
    @NonNull AbiEntryType type,
    @Nullable String name,
    @NonNull List<AbiParameter> inputs,
    @NonNull List<AbiParameter> outputs,
    @NonNull StateMutability stateMutability)
    implements AbiEntry {

  /**
   * Constructs a new {@code AbiFunction} with the specified type, name, input parameters, output
   * parameters, and state mutability.
   *
   * @param type the type of the function
   * @param name the name of the function
   * @param inputs the input parameters of the function
   * @param outputs the output parameters of the function
   * @param stateMutability the state mutability of the function
   */
  public AbiFunction(
      @NonNull AbiEntryType type,
      @Nullable String name,
      @NonNull List<AbiParameter> inputs,
      @NonNull List<AbiParameter> outputs,
      @NonNull StateMutability stateMutability) {
    this.type = Objects.requireNonNull(type, "type");
    if (!type.isCompatibleWithFunction()) {
      throw new IllegalArgumentException("type must be compatible for a function");
    }
    if (type == AbiEntryType.FUNCTION) {
      if (name == null) {
        throw new IllegalArgumentException("name must be provided for a function");
      }
    }
    if (type == AbiEntryType.CONSTRUCTOR
        || type == AbiEntryType.RECEIVE
        || type == AbiEntryType.FALLBACK) {
      if (name != null) {
        throw new IllegalArgumentException("name must not be provided for a type '" + type + "'");
      }
      if (!outputs.isEmpty()) {
        throw new IllegalArgumentException("output must be empty for type " + type + "'");
      }
    }
    if (type == AbiEntryType.RECEIVE || type == AbiEntryType.FALLBACK) {
      if (!inputs.isEmpty()) {
        throw new IllegalArgumentException("inputs must be empty for type " + type + "'");
      }
    }
    this.name = name;
    Objects.requireNonNull(inputs, "inputs");
    this.inputs = List.copyOf(inputs);
    Objects.requireNonNull(outputs, "outputs");
    this.outputs = List.copyOf(outputs);
    this.stateMutability = Objects.requireNonNull(stateMutability, "stateMutability");
  }
}
