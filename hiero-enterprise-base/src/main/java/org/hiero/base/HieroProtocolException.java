package org.hiero.base;

import com.hedera.hashgraph.sdk.Status;
import org.jspecify.annotations.NonNull;

public class HieroProtocolException extends HieroBaseException {
  private final Status status;

  public HieroProtocolException(@NonNull String message, @NonNull Status status) {
    super(message + " (Status: " + status + ")");
    this.status = status;
  }

  public HieroProtocolException(@NonNull String message, @NonNull Status status, @NonNull Throwable cause) {
    super(message + " (Status: " + status + ")", cause);
    this.status = status;
  }

  @NonNull
  public Status getStatus() {
    return status;
  }
}
