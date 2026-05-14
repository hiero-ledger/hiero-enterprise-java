package org.hiero.base;

import org.jspecify.annotations.NonNull;

/** Represents an exception that occurred while interacting with a Hiero network. */
public class HieroBaseException extends Exception {

  /**
   * Constructs a new Exception with the specified detail message.
   *
   * @param message The detail message.
   */
  public HieroBaseException(@NonNull String message) {
    super(message);
  }

  /**
   * Constructs a new Exception with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause.
   */
  public HieroBaseException(@NonNull String message, @NonNull Throwable cause) {
    super(message, cause);
  }
}
