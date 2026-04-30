package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.TokenId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.TokenUpdateBuilder;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.TokenUpdateRequest;
import org.hiero.base.protocol.data.TokenUpdateResult;
import org.hiero.base.protocol.data.TransactionRequest;
import org.jspecify.annotations.NonNull;

public class TokenUpdateBuilderImpl implements TokenUpdateBuilder {

  private final ProtocolLayerClient protocolLayerClient;
  private final TokenId tokenId;
  private String name;
  private String symbol;
  private AccountId treasuryAccountId;
  private Key adminKey;
  private Key kycKey;
  private Key freezeKey;
  private Key wipeKey;
  private Key supplyKey;
  private Key feeScheduleKey;
  private Key pauseKey;
  private Key metadataKey;
  private String memo;
  private AccountId autoRenewAccountId;
  private Duration autoRenewPeriod;
  private Instant expirationTime;

  public TokenUpdateBuilderImpl(
      @NonNull final ProtocolLayerClient protocolLayerClient, @NonNull final TokenId tokenId) {
    this.protocolLayerClient =
        Objects.requireNonNull(protocolLayerClient, "protocolLayerClient must not be null");
    this.tokenId = Objects.requireNonNull(tokenId, "tokenId must not be null");
  }

  @Override
  public @NonNull TokenUpdateBuilder name(@NonNull String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder symbol(@NonNull String symbol) {
    this.symbol = Objects.requireNonNull(symbol, "symbol must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder treasuryAccountId(@NonNull AccountId treasuryAccountId) {
    this.treasuryAccountId =
        Objects.requireNonNull(treasuryAccountId, "treasuryAccountId must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder adminKey(@NonNull Key adminKey) {
    this.adminKey = Objects.requireNonNull(adminKey, "adminKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder kycKey(@NonNull Key kycKey) {
    this.kycKey = Objects.requireNonNull(kycKey, "kycKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder freezeKey(@NonNull Key freezeKey) {
    this.freezeKey = Objects.requireNonNull(freezeKey, "freezeKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder wipeKey(@NonNull Key wipeKey) {
    this.wipeKey = Objects.requireNonNull(wipeKey, "wipeKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder supplyKey(@NonNull Key supplyKey) {
    this.supplyKey = Objects.requireNonNull(supplyKey, "supplyKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder feeScheduleKey(@NonNull Key feeScheduleKey) {
    this.feeScheduleKey = Objects.requireNonNull(feeScheduleKey, "feeScheduleKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder pauseKey(@NonNull Key pauseKey) {
    this.pauseKey = Objects.requireNonNull(pauseKey, "pauseKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder metadataKey(@NonNull Key metadataKey) {
    this.metadataKey = Objects.requireNonNull(metadataKey, "metadataKey must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder memo(@NonNull String memo) {
    this.memo = Objects.requireNonNull(memo, "memo must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder autoRenewAccountId(@NonNull AccountId autoRenewAccountId) {
    this.autoRenewAccountId =
        Objects.requireNonNull(autoRenewAccountId, "autoRenewAccountId must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder autoRenewPeriod(@NonNull Duration autoRenewPeriod) {
    this.autoRenewPeriod =
        Objects.requireNonNull(autoRenewPeriod, "autoRenewPeriod must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateBuilder expirationTime(@NonNull Instant expirationTime) {
    this.expirationTime = Objects.requireNonNull(expirationTime, "expirationTime must not be null");
    return this;
  }

  @Override
  public @NonNull TokenUpdateResult execute() throws HieroException {
    final TokenUpdateRequest request =
        new TokenUpdateRequest(
            tokenId,
            name,
            symbol,
            treasuryAccountId,
            adminKey,
            kycKey,
            freezeKey,
            wipeKey,
            supplyKey,
            feeScheduleKey,
            pauseKey,
            metadataKey,
            memo,
            autoRenewAccountId,
            autoRenewPeriod,
            expirationTime,
            Hbar.from(100),
            TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION);
    return protocolLayerClient.executeTokenUpdateTransaction(request);
  }
}
