package com.openelements.hiero.smartcontract.abi.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Represents an error in the ABI (Application Binary Interface) of a smart contract.
 *
 * @param name the name of the error
 * @param inputs the input parameters of the error
 */
public record AbiError(@NonNull String name, @NonNull List<AbiParameter> inputs)
    implements AbiEntry {

  /**
   * Constructs a new {@code AbiError} with the specified name and input parameters.
   *
   * @param name the name of the error
   * @param inputs the input parameters of the error
   */
  public AbiError(@NonNull String name, @NonNull List<AbiParameter> inputs) {
    this.name = Objects.requireNonNull(name, "name");
    Objects.requireNonNull(inputs, "inputs");
    this.inputs = Collections.unmodifiableList(inputs);
  }

  @Override
  public AbiEntryType type() {
    return AbiEntryType.ERROR;
  }
}
