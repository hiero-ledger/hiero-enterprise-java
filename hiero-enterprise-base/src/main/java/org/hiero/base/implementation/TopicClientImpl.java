package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.SubscriptionHandle;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessage;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
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

  @Override
  public SubscriptionHandle subscribeTopic(
      @NonNull TopicId topicId, @NonNull Consumer<TopicMessage> handler) throws HieroException {
    return subscribeTopic(topicId, handler, null, null, -1);
  }

  @Override
  public SubscriptionHandle subscribeTopic(
      @NonNull TopicId topicId, @NonNull Consumer<TopicMessage> handler, long limit)
      throws HieroException {
    return subscribeTopic(topicId, handler, null, null, limit);
  }

  @Override
  public SubscriptionHandle subscribeTopic(
      @NonNull TopicId topicId,
      @NonNull Consumer<TopicMessage> handler,
      @NonNull Instant startTime,
      @NonNull Instant endTime)
      throws HieroException {
    return subscribeTopic(topicId, handler, startTime, endTime, -1);
  }

  @Override
  public SubscriptionHandle subscribeTopic(
      @NonNull TopicId topicId,
      @NonNull Consumer<TopicMessage> handler,
      @Nullable Instant startTime,
      @Nullable Instant endTime,
      long limit)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    Objects.requireNonNull(handler, "handler must not be null");

    if (limit < -1) {
      throw new IllegalArgumentException("limit must be -1 (infinite) or greater than 0");
    }

    if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
      throw new IllegalArgumentException("endTime cannot be before startTime");
    }

    TopicMessageRequest request =
        TopicMessageRequest.of(topicId, handler, startTime, endTime, limit);
    TopicMessageResult result = client.executeTopicMessageQuery(request);
    return result.subscriptionHandle();
  }
}
