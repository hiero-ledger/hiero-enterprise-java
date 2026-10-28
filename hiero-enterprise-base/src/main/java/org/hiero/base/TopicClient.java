package org.hiero.base;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.hiero.base.data.Account;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Interface for interacting with a Hiero network. This interface provides methods for interacting
 * with Hiero Topic, like creating, updating and deleting topic. An implementation of this interface
 * is using an internal account to interact with a Hiero network. That account is the account that
 * is used to pay for the transactions that are sent to the Hiero network and called 'operator
 * account'.
 */
public interface TopicClient {
  /**
   * Create a new Topic. The operator account privateKey is used as adminKey for topic.
   *
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createTopic() throws HieroException;

  /**
   * Create a new Topic. With an adminKey.
   *
   * @param adminKey the adminKey for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createTopic(@NonNull PrivateKey adminKey) throws HieroException;

  /**
   * Create a new Topic.
   *
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createTopic(@NonNull String memo) throws HieroException;

  /**
   * Create a new Topic.
   *
   * @param adminKey the adminKey for the topic
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createTopic(@NonNull PrivateKey adminKey, @NonNull String memo)
      throws HieroException;

  /**
   * Create a new private Topic.
   *
   * @param submitKey the submitKey for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(@NonNull PrivateKey submitKey) throws HieroException;

  /**
   * Create a new private Topic.
   *
   * @param adminKey the adminKey for the topic
   * @param submitKey the submitKey for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(@NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey)
      throws HieroException;

  /**
   * Create a new private Topic.
   *
   * @param submitKey the submitKey for the topic
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(@NonNull PrivateKey submitKey, @NonNull String memo)
      throws HieroException;

  /**
   * Create a new private Topic.
   *
   * @param adminKey the adminKey for the topic
   * @param submitKey the submitKey for the topic
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(
      @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey, @NonNull String memo)
      throws HieroException;

  /**
   * Create a new immutable Topic. A topic without an admin key cannot be updated or deleted; only
   * its expiration time can be extended.
   *
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createImmutableTopic() throws HieroException;

  /**
   * Create a new immutable Topic. A topic without an admin key cannot be updated or deleted; only
   * its expiration time can be extended.
   *
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createImmutableTopic(@NonNull String memo) throws HieroException;

  /**
   * Create a new Topic with full control over its keys and auto renew settings.
   *
   * @param adminKey the adminKey for the topic, or null to create an immutable topic
   * @param submitKey the submitKey for the topic, or null to create a public topic
   * @param memo the memo for the topic, or null
   * @param autoRenewAccount the account that pays for auto renewal and signs the transaction, or
   *     null to use the operator account
   * @param autoRenewPeriod the initial lifetime and auto renew period of the topic (roughly 30 to
   *     92 days), or null to use the network default
   * @return the ID of the new Topic
   * @throws HieroException if the Topic could not be created
   */
  @NonNull TopicId createTopic(
      @Nullable PrivateKey adminKey,
      @Nullable PrivateKey submitKey,
      @Nullable String memo,
      @Nullable Account autoRenewAccount,
      @Nullable Duration autoRenewPeriod)
      throws HieroException;

  /**
   * Update a Topic. The operator account privateKey is used as adminKey for updating topic.
   *
   * @param topicId the topicId of topic to update
   * @param memo the memo for the topic
   * @throws HieroException if the Topic could not be created
   */
  void updateTopic(@NonNull TopicId topicId, @NonNull String memo) throws HieroException;

  /**
   * Update a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey for topic
   * @param memo the memo for the topic
   * @throws HieroException if the Topic could not be created
   */
  void updateTopic(@NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull String memo)
      throws HieroException;

  /**
   * Update a Topic. The operator account privateKey is used as adminKey for updating topic.
   *
   * @param topicId the topicId of topic to update
   * @param updatedAdminKey the new adminKey for topic
   * @param submitKey the new submit for topic
   * @param memo the memo for the topic
   * @throws HieroException if the Topic could not be created
   */
  void updateTopic(
      @NonNull TopicId topicId,
      @NonNull PrivateKey updatedAdminKey,
      @NonNull PrivateKey submitKey,
      @NonNull String memo)
      throws HieroException;

  /**
   * Update a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param updatedAdminKey the new adminKey for topic
   * @param submitKey the new submit for topic
   * @param memo the memo for the topic
   * @throws HieroException if the Topic could not be created
   */
  void updateTopic(
      @NonNull TopicId topicId,
      @NonNull PrivateKey adminKey,
      @NonNull PrivateKey updatedAdminKey,
      @NonNull PrivateKey submitKey,
      @NonNull String memo)
      throws HieroException;

  /**
   * Update adminKey for Topic. The operator account privateKey is used as adminKey for updating
   * topic.
   *
   * @param topicId the topicId of topic to update
   * @param updatedAdminKey the new adminKey for topic
   * @throws HieroException if the Topic could not be created
   */
  void updateAdminKey(@NonNull TopicId topicId, @NonNull PrivateKey updatedAdminKey)
      throws HieroException;

  /**
   * Update adminKey for Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param updatedAdminKey the new adminKey for topic
   * @throws HieroException if the Topic could not be created
   */
  void updateAdminKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull PrivateKey updatedAdminKey)
      throws HieroException;

  /**
   * Update submitKey for Topic. The operator account privateKey is used as adminKey for updating
   * topic.
   *
   * @param topicId the topicId of topic to update
   * @param submitKey the new submitKey for topic
   * @throws HieroException if the Topic could not be created
   */
  void updateSubmitKey(@NonNull TopicId topicId, @NonNull PrivateKey submitKey)
      throws HieroException;

  /**
   * Update submitKey for Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param submitKey the new submitKey for topic
   * @throws HieroException if the Topic could not be created
   */
  void updateSubmitKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey)
      throws HieroException;

  /**
   * Update the auto renew account of a Topic. The operator account privateKey is used as adminKey.
   * The new auto renew account must sign the transaction.
   *
   * @param topicId the topicId of topic to update
   * @param autoRenewAccount the new auto renew account
   * @throws HieroException if the Topic could not be updated
   */
  void updateAutoRenewAccount(@NonNull TopicId topicId, @NonNull Account autoRenewAccount)
      throws HieroException;

  /**
   * Update the auto renew account of a Topic. The new auto renew account must sign the transaction.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param autoRenewAccount the new auto renew account
   * @throws HieroException if the Topic could not be updated
   */
  void updateAutoRenewAccount(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull Account autoRenewAccount)
      throws HieroException;

  /**
   * Remove the auto renew account of a Topic. The operator account privateKey is used as adminKey.
   *
   * @param topicId the topicId of topic to update
   * @throws HieroException if the Topic could not be updated
   */
  void clearAutoRenewAccount(@NonNull TopicId topicId) throws HieroException;

  /**
   * Remove the auto renew account of a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @throws HieroException if the Topic could not be updated
   */
  void clearAutoRenewAccount(@NonNull TopicId topicId, @NonNull PrivateKey adminKey)
      throws HieroException;

  /**
   * Update the auto renew period of a Topic. The operator account privateKey is used as adminKey.
   *
   * @param topicId the topicId of topic to update
   * @param autoRenewPeriod the new auto renew period (roughly 30 to 92 days)
   * @throws HieroException if the Topic could not be updated
   */
  void updateAutoRenewPeriod(@NonNull TopicId topicId, @NonNull Duration autoRenewPeriod)
      throws HieroException;

  /**
   * Update the auto renew period of a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param autoRenewPeriod the new auto renew period (roughly 30 to 92 days)
   * @throws HieroException if the Topic could not be updated
   */
  void updateAutoRenewPeriod(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull Duration autoRenewPeriod)
      throws HieroException;

  /**
   * Extend the expiration time of a Topic. No admin key is required, so this also works for
   * immutable topics. The new expiration time must be later than the current one and at most
   * roughly 92 days in the future.
   *
   * @param topicId the topicId of topic to update
   * @param expirationTime the new expiration time
   * @throws HieroException if the Topic could not be updated
   */
  void updateExpirationTime(@NonNull TopicId topicId, @NonNull Instant expirationTime)
      throws HieroException;

  /**
   * Remove the admin key of a Topic, making it immutable. The operator account privateKey is used
   * as adminKey. After this, the topic can no longer be updated or deleted; only its expiration
   * time can be extended. The auto renew account is removed as well, because the network does not
   * allow a topic without an admin key to have one.
   *
   * @param topicId the topicId of topic to update
   * @throws HieroException if the Topic could not be updated
   */
  void clearAdminKey(@NonNull TopicId topicId) throws HieroException;

  /**
   * Remove the admin key of a Topic, making it immutable. After this, the topic can no longer be
   * updated or deleted; only its expiration time can be extended. The auto renew account is removed
   * as well, because the network does not allow a topic without an admin key to have one.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @throws HieroException if the Topic could not be updated
   */
  void clearAdminKey(@NonNull TopicId topicId, @NonNull PrivateKey adminKey) throws HieroException;

  /**
   * Remove the submit key of a Topic, making it public. The operator account privateKey is used as
   * adminKey.
   *
   * @param topicId the topicId of topic to update
   * @throws HieroException if the Topic could not be updated
   */
  void clearSubmitKey(@NonNull TopicId topicId) throws HieroException;

  /**
   * Remove the submit key of a Topic, making it public.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @throws HieroException if the Topic could not be updated
   */
  void clearSubmitKey(@NonNull TopicId topicId, @NonNull PrivateKey adminKey) throws HieroException;

  /**
   * Remove the memo of a Topic. The operator account privateKey is used as adminKey.
   *
   * @param topicId the topicId of topic to update
   * @throws HieroException if the Topic could not be updated
   */
  void clearTopicMemo(@NonNull TopicId topicId) throws HieroException;

  /**
   * Remove the memo of a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @throws HieroException if the Topic could not be updated
   */
  void clearTopicMemo(@NonNull TopicId topicId, @NonNull PrivateKey adminKey) throws HieroException;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroException if the Topic could not be created
   */
  void deleteTopic(@NonNull TopicId topicId) throws HieroException;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroException if the Topic could not be created
   */
  default void deleteTopic(@NonNull String topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    deleteTopic(TopicId.fromString(topicId));
  }
  ;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroException if the Topic could not be created
   */
  void deleteTopic(@NonNull TopicId topicId, @NonNull PrivateKey adminKey) throws HieroException;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroException if the Topic could not be created
   */
  default void deleteTopic(@NonNull String topicId, @NonNull String adminKey)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(adminKey, "adminKey cannot be null");
    deleteTopic(TopicId.fromString(topicId), PrivateKey.fromStringDER(adminKey));
  }
  ;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  void submitMessage(@NonNull TopicId topicId, @NonNull byte[] message) throws HieroException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  default void submitMessage(@NonNull String topicId, @NonNull byte[] message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(message, "message cannot be null");
    submitMessage(TopicId.fromString(topicId), message);
  }
  ;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  void submitMessage(@NonNull TopicId topicId, @NonNull String message) throws HieroException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  default void submitMessage(@NonNull String topicId, @NonNull String message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(message, "message cannot be null");
    submitMessage(TopicId.fromString(topicId), message);
  }
  ;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param submitKey the submit key for submitting message
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  void submitMessage(
      @NonNull TopicId topicId, @NonNull PrivateKey submitKey, @NonNull byte[] message)
      throws HieroException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  default void submitMessage(
      @NonNull String topicId, @NonNull String submitKey, @NonNull byte[] message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(submitKey, "submitKey cannot be null");
    Objects.requireNonNull(message, "message cannot be null");
    submitMessage(TopicId.fromString(topicId), PrivateKey.fromStringDER(submitKey), message);
  }
  ;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param submitKey the submit key for submitting message
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  void submitMessage(
      @NonNull TopicId topicId, @NonNull PrivateKey submitKey, @NonNull String message)
      throws HieroException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroException if the Topic could not be created
   */
  default void submitMessage(
      @NonNull String topicId, @NonNull String submitKey, @NonNull String message)
      throws HieroException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(submitKey, "submitKey cannot be null");
    Objects.requireNonNull(message, "message cannot be null");
    submitMessage(TopicId.fromString(topicId), PrivateKey.fromStringDER(submitKey), message);
  }
  ;
}
