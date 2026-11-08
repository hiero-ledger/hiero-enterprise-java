package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.hiero.base.HieroException;
import org.hiero.base.TopicClient;
import org.hiero.base.data.Account;
import org.hiero.base.protocol.ProtocolLayerClient;
import org.hiero.base.protocol.data.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TopicClientImpl implements TopicClient {
  private final ProtocolLayerClient client;

  private final Account operationalAccount;

  public TopicClientImpl(
      @NonNull final ProtocolLayerClient client, @NonNull final Account operationalAccount) {
    this.client = Objects.requireNonNull(client, "client must not be null");
    this.operationalAccount =
        Objects.requireNonNull(operationalAccount, "operationalAccount must not be null");
  }

  @Override
  public @NonNull TopicId createTopic() throws HieroException {
    return createTopic(operationalAccount.privateKey());
  }

  @Override
  public @NonNull TopicId createTopic(@NonNull PrivateKey adminKey) throws HieroException {
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    TopicCreateRequest request = TopicCreateRequest.of(adminKey);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public @NonNull TopicId createTopic(@NonNull String memo) throws HieroException {
    Objects.requireNonNull(memo, "memo must not be null");
    return createTopic(operationalAccount.privateKey(), memo);
  }

  @Override
  public @NonNull TopicId createTopic(@NonNull PrivateKey adminKey, @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    TopicCreateRequest request = TopicCreateRequest.of(adminKey, memo);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public @NonNull TopicId createPrivateTopic(@NonNull PrivateKey submitKey) throws HieroException {
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    return createPrivateTopic(operationalAccount.privateKey(), submitKey);
  }

  @Override
  public @NonNull TopicId createPrivateTopic(
      @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey) throws HieroException {
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    TopicCreateRequest request = TopicCreateRequest.of(adminKey, submitKey);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public @NonNull TopicId createPrivateTopic(@NonNull PrivateKey submitKey, @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    return createPrivateTopic(operationalAccount.privateKey(), submitKey, memo);
  }

  @Override
  public @NonNull TopicId createPrivateTopic(
      @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey, @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    TopicCreateRequest request = TopicCreateRequest.of(adminKey, submitKey, memo);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public @NonNull TopicId createImmutableTopic() throws HieroException {
    TopicCreateRequest request = TopicCreateRequest.immutable(null, null);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public @NonNull TopicId createImmutableTopic(@NonNull String memo) throws HieroException {
    Objects.requireNonNull(memo, "memo must not be null");
    TopicCreateRequest request = TopicCreateRequest.immutable(null, memo);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public @NonNull TopicId createTopic(
      @Nullable PrivateKey adminKey,
      @Nullable PrivateKey submitKey,
      @Nullable String memo,
      @Nullable Account autoRenewAccount,
      @Nullable Duration autoRenewPeriod)
      throws HieroException {
    TopicCreateRequest request =
        TopicCreateRequest.of(adminKey, submitKey, memo, autoRenewAccount, autoRenewPeriod);
    TopicCreateResult result = client.executeTopicCreateTransaction(request);
    return result.topicId();
  }

  @Override
  public void updateTopic(@NonNull TopicId topicId, @NonNull String memo) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    updateTopic(topicId, operationalAccount.privateKey(), memo);
  }

  @Override
  public void updateTopic(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.of(topicId, adminKey, memo);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void updateTopic(
      @NonNull TopicId topicId,
      @NonNull PrivateKey updatedAdminKey,
      @NonNull PrivateKey submitKey,
      @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    updateTopic(topicId, operationalAccount.privateKey(), updatedAdminKey, submitKey, memo);
  }

  @Override
  public void updateTopic(
      @NonNull TopicId topicId,
      @NonNull PrivateKey adminKey,
      @NonNull PrivateKey updatedAdminKey,
      @NonNull PrivateKey submitKey,
      @NonNull String memo)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    Objects.requireNonNull(memo, "memo must not be null");
    TopicUpdateRequest request =
        TopicUpdateRequest.of(topicId, adminKey, updatedAdminKey, submitKey, memo);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void updateAdminKey(@NonNull TopicId topicId, @NonNull PrivateKey updatedAdminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(updatedAdminKey, "updatedAdminKey must not be null");
    updateAdminKey(topicId, operationalAccount.privateKey(), updatedAdminKey);
  }

  @Override
  public void updateAdminKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull PrivateKey updatedAdminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(updatedAdminKey, "updatedAdminKey must not be null");
    TopicUpdateRequest request =
        TopicUpdateRequest.updateAdminKey(topicId, adminKey, updatedAdminKey);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void updateSubmitKey(@NonNull TopicId topicId, @NonNull PrivateKey submitKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    updateSubmitKey(topicId, operationalAccount.privateKey(), submitKey);
  }

  @Override
  public void updateSubmitKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.updateSubmitKey(topicId, adminKey, submitKey);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void updateAutoRenewAccount(@NonNull TopicId topicId, @NonNull Account autoRenewAccount)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(autoRenewAccount, "autoRenewAccount must not be null");
    updateAutoRenewAccount(topicId, operationalAccount.privateKey(), autoRenewAccount);
  }

  @Override
  public void updateAutoRenewAccount(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull Account autoRenewAccount)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(autoRenewAccount, "autoRenewAccount must not be null");
    TopicUpdateRequest request =
        TopicUpdateRequest.updateAutoRenewAccount(topicId, adminKey, autoRenewAccount);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void clearAutoRenewAccount(@NonNull TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    clearAutoRenewAccount(topicId, operationalAccount.privateKey());
  }

  @Override
  public void clearAutoRenewAccount(@NonNull TopicId topicId, @NonNull PrivateKey adminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.clearAutoRenewAccount(topicId, adminKey);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void updateAutoRenewPeriod(@NonNull TopicId topicId, @NonNull Duration autoRenewPeriod)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(autoRenewPeriod, "autoRenewPeriod must not be null");
    updateAutoRenewPeriod(topicId, operationalAccount.privateKey(), autoRenewPeriod);
  }

  @Override
  public void updateAutoRenewPeriod(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull Duration autoRenewPeriod)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    Objects.requireNonNull(autoRenewPeriod, "autoRenewPeriod must not be null");
    TopicUpdateRequest request =
        TopicUpdateRequest.updateAutoRenewPeriod(topicId, adminKey, autoRenewPeriod);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void updateExpirationTime(@NonNull TopicId topicId, @NonNull Instant expirationTime)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(expirationTime, "expirationTime must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.updateExpirationTime(topicId, expirationTime);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void clearAdminKey(@NonNull TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    clearAdminKey(topicId, operationalAccount.privateKey());
  }

  @Override
  public void clearAdminKey(@NonNull TopicId topicId, @NonNull PrivateKey adminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.clearAdminKey(topicId, adminKey);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void clearSubmitKey(@NonNull TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    clearSubmitKey(topicId, operationalAccount.privateKey());
  }

  @Override
  public void clearSubmitKey(@NonNull TopicId topicId, @NonNull PrivateKey adminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.clearSubmitKey(topicId, adminKey);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void clearTopicMemo(@NonNull TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    clearTopicMemo(topicId, operationalAccount.privateKey());
  }

  @Override
  public void clearTopicMemo(@NonNull TopicId topicId, @NonNull PrivateKey adminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    TopicUpdateRequest request = TopicUpdateRequest.clearMemo(topicId, adminKey);
    client.executeTopicUpdateTransaction(request);
  }

  @Override
  public void deleteTopic(@NonNull TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    TopicDeleteRequest request = TopicDeleteRequest.of(operationalAccount.privateKey(), topicId);
    client.executeTopicDeleteTransaction(request);
  }

  @Override
  public void deleteTopic(@NonNull TopicId topicId, @NonNull PrivateKey adminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(adminKey, "adminKey must not be null");
    TopicDeleteRequest request = TopicDeleteRequest.of(adminKey, topicId);
    client.executeTopicDeleteTransaction(request);
  }

  @Override
  public void submitMessage(@NonNull TopicId topicId, @NonNull String message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(message, "message must not be null");
    submitMessage(topicId, message.getBytes());
  }

  @Override
  public void submitMessage(@NonNull TopicId topicId, @NonNull byte[] message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(message, "message must not be null");
    TopicSubmitMessageRequest request = TopicSubmitMessageRequest.of(topicId, message);
    client.executeTopicMessageSubmitTransaction(request);
  }

  @Override
  public void submitMessage(
      @NonNull TopicId topicId, @NonNull PrivateKey submitKey, @NonNull String message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    Objects.requireNonNull(message, "message must not be null");
    submitMessage(topicId, submitKey, message.getBytes());
  }

  @Override
  public void submitMessage(
      @NonNull TopicId topicId, @NonNull PrivateKey submitKey, @NonNull byte[] message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(submitKey, "submitKey must not be null");
    Objects.requireNonNull(message, "message must not be null");
    TopicSubmitMessageRequest request = TopicSubmitMessageRequest.of(topicId, submitKey, message);
    client.executeTopicMessageSubmitTransaction(request);
  }
}
