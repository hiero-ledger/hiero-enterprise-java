package com.openelements.hiero.smartcontract.abi.model;

import java.util.List;
import org.jspecify.annotations.NonNull;

/**
 * Represents an entry in the ABI (Application Binary Interface) of a smart contract. More
 * information can be found at <a
 * href="https://docs.soliditylang.org/en/latest/abi-spec.html">Solidity ABI Specification</a>.
 */
public sealed interface AbiEntry permits AbiEvent, AbiError, AbiFunction {

  /**
   * Returns the type of the ABI entry.
   *
   * @return the type of the ABI entry
   */
  @NonNull AbiEntryType type();

  /**
   * Returns the name of the ABI entry.
   *
   * @return the name of the ABI entry
   */
  @NonNull String name();

  /**
   * Returns the input parameters of the ABI entry.
   *
   * @return the input parameters of the ABI entry
   */
  @NonNull List<AbiParameter> inputs();
}
