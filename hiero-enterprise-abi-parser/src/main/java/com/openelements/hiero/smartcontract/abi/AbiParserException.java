package com.openelements.hiero.smartcontract.abi;

import org.jspecify.annotations.NonNull;

/** Exception thrown when an error occurs while parsing an ABI file. */
public class AbiParserException extends Exception {

  /**
   * Constructs a new {@code AbiParserException} with the specified detail message.
   *
   * @param message the detail message
   */
  public AbiParserException(@NonNull final String message) {
    super(message);
  }

  /**
   * Constructs a new {@code AbiParserException} with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public AbiParserException(@NonNull final String message, @NonNull final Throwable cause) {
    super(message, cause);
  }
}
