package org.hiero.base.event;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;

/**
 * Wrapper for Web3j {@link TypeReference} for create smart contract event params.
 *
 * @param typeReference the {@link TypeReference} representing the event params (ABI types, and
 *     index flag)
 */
public record EventParam(TypeReference<?> typeReference) {
    public EventParam {
        Objects.requireNonNull(typeReference, "typeReference must not be null");
    }

    /**
     * Creates address event param.
     *
     * @param indexed represent if parameter is an indexed param in event
     * @return a new instance of {@code EventParam} for {@link Address}
     */
    public static @NonNull EventParam address(boolean indexed) {
        return new EventParam(TypeReference.create(Address.class, indexed));
    }

    /**
     * Creates string event param.
     *
     * @param indexed represent if parameter is an indexed param in event
     * @return a new instance of {@code EventParam} for {@link Utf8String}
     */
    public static @NonNull EventParam string(boolean indexed) {
        return new EventParam(TypeReference.create(Utf8String.class, indexed));
    }

    /**
     * Creates bool event param.
     *
     * @param indexed represent if parameter is an indexed param in event
     * @return a new instance of {@code EventParam} for {@link Bool}
     */
    public static @NonNull EventParam bool(boolean indexed) {
        return new EventParam(TypeReference.create(Bool.class, indexed));
    }

    /**
     * Creates bytes event param.
     *
     * @param indexed represent if parameter is an indexed param in event
     * @return a new instance of {@code EventParam} for {@link Bytes32}
     */
    public static @NonNull EventParam bytes(boolean indexed) {
        return new EventParam(TypeReference.create(Bytes32.class, indexed));
    }

    /**
     * Creates uint256 event param.
     *
     * @param indexed represent if parameter is an indexed param in event
     * @return a new instance of {@code EventParam} for {@link Uint256}
     */
    public static @NonNull EventParam uint256(boolean indexed) {
        return new EventParam(TypeReference.create(Uint256.class, indexed));
    }

    /**
     * Creates int256 event param.
     *
     * @param indexed represent if parameter is an indexed param in event
     * @return a new instance of {@code EventParam} for {@link Int256}
     */
    public static @NonNull EventParam int256(boolean indexed) {
        return new EventParam(TypeReference.create(Int256.class, indexed));
    }

    // TODO: Add remaining types...
}