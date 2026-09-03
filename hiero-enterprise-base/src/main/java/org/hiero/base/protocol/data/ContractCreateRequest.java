package org.hiero.base.protocol.data;

import static org.hiero.base.implementation.ProtocolLayerClientImpl.MAX_GAS_LIMIT;

import com.hedera.hashgraph.sdk.FileId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import org.hiero.base.data.ContractParam;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ContractCreateRequest(
    @NonNull Hbar maxTransactionFee,
    @NonNull Duration transactionValidDuration,
    @NonNull FileId fileId,
    int gas,
    @NonNull PrivateKey adminKey,
    @NonNull List<ContractParam<?>> constructorParams)
    implements TransactionRequest {

  public static final Hbar DEFAULT_CONTRACT_CREATE_TRANSACTION_FEE = Hbar.from(100);

  public ContractCreateRequest {
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee is required");
    Objects.requireNonNull(transactionValidDuration, "transactionValidDuration is required");
    Objects.requireNonNull(fileId, "fileId is required");
    Objects.requireNonNull(constructorParams, "constructorParams is required");
    Objects.requireNonNull(adminKey, "adminKey must not be null");

    if (maxTransactionFee.toTinybars() < 0) {
      throw new IllegalArgumentException("maxTransactionFee must be non-negative");
    }
    if (transactionValidDuration.isNegative() || transactionValidDuration.isZero()) {
      throw new IllegalArgumentException("transactionValidDuration must be positive");
    }
    if (gas < 0 || gas > MAX_GAS_LIMIT) {
      throw new IllegalArgumentException(
          "gas must be between 0 and " + MAX_GAS_LIMIT + " inclusive");
    }
  }

  @NonNull
  public static ContractCreateRequest of(
      @NonNull String fileId,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @NonNull PrivateKey adminKey,
      @Nullable ContractParam<?>... constructorParams) {
    Objects.requireNonNull(fileId, "fileId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    return of(FileId.fromString(fileId), maxTransactionFee, gas, adminKey, constructorParams);
  }

  @NonNull
  public static ContractCreateRequest of(
      @NonNull FileId fileId,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @NonNull PrivateKey adminKey,
      @Nullable ContractParam<?>... constructorParams) {
    if (constructorParams == null) {
      return of(fileId, maxTransactionFee, gas, adminKey, List.of());
    } else {
      return of(fileId, maxTransactionFee, gas, adminKey, List.of(constructorParams));
    }
  }

  @NonNull
  public static ContractCreateRequest of(
      @NonNull String fileId,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @NonNull PrivateKey adminKey,
      @NonNull List<ContractParam<?>> constructorParams) {
    Objects.requireNonNull(fileId, "fileId must not be null");
    Objects.requireNonNull(maxTransactionFee, "maxTransactionFee must not be null");
    return of(FileId.fromString(fileId), maxTransactionFee, gas, adminKey, constructorParams);
  }

  @NonNull
  public static ContractCreateRequest of(
      @NonNull FileId fileId,
      @NonNull Hbar maxTransactionFee,
      int gas,
      @NonNull PrivateKey adminKey,
      @NonNull List<ContractParam<?>> constructorParams) {
    return new ContractCreateRequest(
        maxTransactionFee,
        DEFAULT_TRANSACTION_VALID_DURATION,
        fileId,
        gas,
        adminKey,
        List.copyOf(constructorParams));
  }
}
