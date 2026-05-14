package org.hiero.base;

public class HieroSecurityException extends HieroBaseException {
  public HieroSecurityException(String message) {
    super(message);
  }

  public HieroSecurityException(String message, Throwable cause) {
    super(message, cause);
  }
}
