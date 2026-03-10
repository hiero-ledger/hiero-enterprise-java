package com.openelements.hiero.smartcontract.abi.model;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Represents the ABI (Application Binary Interface) model of a smart contract.
 *
 * @param entries the list of ABI entries
 */
public record AbiModel(@NonNull List<AbiEntry> entries) {

  /**
   * Constructs a new {@code AbiModel} with the specified list of ABI entries.
   *
   * @param entries the list of ABI entries
   */
  public AbiModel(@NonNull List<AbiEntry> entries) {
    Objects.requireNonNull(entries, "entries");
    this.entries = List.copyOf(entries);
  }

  /**
   * Returns the list of ABI functions.
   *
   * @return the list of ABI functions
   */
  @NonNull
  public List<AbiFunction> getFunctions() {
    return getEntriesOfType(AbiFunction.class);
  }

  /**
   * Returns the list of ABI events.
   *
   * @return the list of ABI events
   */
  @NonNull
  public List<AbiEvent> getEvents() {
    return getEntriesOfType(AbiEvent.class);
  }

  /**
   * Returns the list of ABI errors.
   *
   * @return the list of ABI errors
   */
  @NonNull
  public List<AbiError> getErrors() {
    return getEntriesOfType(AbiError.class);
  }

  /**
   * Returns the list of ABI entries of the specified type.
   *
   * @param type the class type of the ABI entry
   * @return the list of ABI entries of the specified type
   * @param <T> the type of the ABI entry (see {@link AbiEntry})
   */
  @NonNull
  private <T extends AbiEntry> List<T> getEntriesOfType(@NonNull final Class<T> type) {
    Objects.requireNonNull(type, "type must not be null");
    return entries.stream().filter(type::isInstance).map(type::cast).toList();
  }
}
