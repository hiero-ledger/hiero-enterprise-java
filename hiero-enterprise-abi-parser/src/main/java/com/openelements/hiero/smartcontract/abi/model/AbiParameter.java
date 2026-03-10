package com.openelements.hiero.smartcontract.abi.model;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Represents a parameter in the ABI (Application Binary Interface) of a smart contract.
 *
 * @param name the name of the parameter
 * @param type the type of the parameter
 * @param components the list of components (if the type is a tuple, see
 *     https://docs.soliditylang.org/en/latest/abi-spec.html#handling-tuple-types)
 * @param indexed indicates if the parameter is indexed
 */
public record AbiParameter(
    @NonNull String name,
    @NonNull AbiParameterType type,
    @NonNull List<AbiParameter> components,
    boolean indexed) {

  /**
   * Constructs a new {@code AbiParameter} with the specified name, type, components, and indexed
   * flag.
   *
   * @param name the name of the parameter
   * @param type the type of the parameter
   * @param components the list of components (if the type is a tuple)
   * @param indexed indicates if the parameter is indexed
   */
  public AbiParameter(
      @NonNull String name,
      @NonNull AbiParameterType type,
      @NonNull List<AbiParameter> components,
      boolean indexed) {
    this.name = Objects.requireNonNull(name, "name");
    this.type = Objects.requireNonNull(type, "type");
    Objects.requireNonNull(components, "components");
    this.components = List.copyOf(components);
    this.indexed = indexed;

    if (type != AbiParameterType.TUPLE && !components.isEmpty()) {
      throw new IllegalStateException(
          "No component structure should be defined for parameter type '" + type + "'");
    }
  }

  /**
   * Returns the canonical type of the parameter.
   *
   * @return the canonical type of the parameter
   */
  @NonNull
  public String getCanonicalType() {
    return type.getCanonicalType();
  }
}
