package org.hiero.base;

public class HieroValidationException extends HieroBaseException {
  public HieroValidationException(String message) {
    super(message);
  }

  public HieroValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
