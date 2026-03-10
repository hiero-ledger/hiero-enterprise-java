package com.openelements.hiero.smartcontract.abi.model;

import com.openelements.hiero.smartcontract.abi.util.HexConverter;
import com.openelements.hiero.smartcontract.abi.util.KeccakUtil;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * Represents an event in the ABI (Application Binary Interface) of a smart contract. More
 * information can be found at <a
 * href="https://docs.soliditylang.org/en/latest/abi-spec.html#events">Solidity ABI Specification
 * for Events</a>.
 *
 * @param name the name of the event
 * @param inputs the input parameters of the event
 * @param anonymous indicates whether the event is anonymous
 */
public record AbiEvent(@NonNull String name, @NonNull List<AbiParameter> inputs, boolean anonymous)
    implements AbiEntry {

  /**
   * Constructs a new {@code AbiEvent} with the specified name, input parameters, and anonymous
   * flag.
   *
   * @param name the name of the event
   * @param inputs the input parameters of the event
   * @param anonymous indicates whether the event is anonymous
   */
  public AbiEvent(@NonNull String name, @NonNull List<AbiParameter> inputs, boolean anonymous) {
    this.name = Objects.requireNonNull(name, "name");
    Objects.requireNonNull(inputs, "inputs");
    this.inputs = Collections.unmodifiableList(inputs);
    this.anonymous = anonymous;
  }

  @Override
  public AbiEntryType type() {
    return AbiEntryType.EVENT;
  }

  /**
   * Returns a list of all indexed input parameters of the event.
   *
   * @return a list of indexed input parameters
   */
  @NonNull
  public List<AbiParameter> getIndexedInputParameters() {
    return inputs.stream().filter(AbiParameter::indexed).toList();
  }

  /**
   * Returns a list of all non-indexed input parameters of the event.
   *
   * @return a list of non-indexed input parameters
   */
  @NonNull
  public List<AbiParameter> getNonIndexedInputParameters() {
    return inputs.stream().filter(parameter -> !parameter.indexed()).toList();
  }

  /**
   * Returns the signature of the event in the format "name(type1,type2,...)".
   *
   * @return the event signature
   */
  @NonNull
  private String createEventSignature() {
    final List<String> canonicalParameterTypes =
        inputs.stream().map(AbiParameter::getCanonicalType).toList();
    return name + "(" + String.join(",", canonicalParameterTypes) + ")";
  }

  /**
   * Returns the Keccak-256 hash of the event signature.
   *
   * @return the Keccak-256 hash of the event signature
   */
  @NonNull
  private byte[] createEventSignatureHash() {
    final String eventSignature = createEventSignature();
    return KeccakUtil.keccak256(eventSignature.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Returns the Keccak-256 hash of the event signature as a hexadecimal string in the format {@code
   * 0x<hash>}.
   *
   * @return the Keccak-256 hash of the event signature as a hexadecimal string
   */
  @NonNull
  public String createEventSignatureHashAsHex() {
    final byte[] eventSignatureHash = createEventSignatureHash();
    return "0x" + HexConverter.bytesToHex(eventSignatureHash);
  }
}
