package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.Hbar;
import java.time.Duration;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record FileAppendRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull FileId fileId,
    @NonNull byte[] contents)
    implements TransactionRequest {

  private static final String DEFAULT_FILE_MEMO = "";

  private static final int FILE_CREATE_MAX_BYTES = 2048;

  public FileAppendRequest {
    Objects.requireNonNull(maxTransactionFee, "Max transaction fee is required");
    Objects.requireNonNull(transactionValidDuration, "Transaction valid duration is required");
    Objects.requireNonNull(fileId, "FileId is required");
    Objects.requireNonNull(contents, "File contents are required");
    if (maxTransactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("maxTransactionFee must be non-negative");
    }
    if (transactionValidDuration.isNegative() || transactionValidDuration.isZero()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (contents.length > FILE_CREATE_MAX_BYTES) {
      throw new IllegalArgumentException(
          "File contents must be less than " + FILE_CREATE_MAX_BYTES + " bytes");
    }
  }

  @NonNull
  public static FileAppendRequest of(@NonNull FileId fileId, @NonNull byte[] contents) {
    return new FileAppendRequest(
        DEFAULT_MAX_TRANSACTION_FEE, DEFAULT_TRANSACTION_VALID_DURATION, fileId, contents);
  }

  @NonNull
  public static FileAppendRequest of(@NonNull String fileId, @NonNull byte[] contents) {
    Objects.requireNonNull(fileId, "FileId must not be null");
    return of(FileId.fromString(fileId), contents);
  }
}
