package org.hiero.base;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.hiero.base.HieroSecurityException;

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
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createTopic() throws HieroBaseException;

  /**
   * Create a new Topic. With an adminKey.
   *
   * @param adminKey the adminKey for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createTopic(@NonNull PrivateKey adminKey) throws HieroBaseException;

  /**
   * Create a new Topic.
   *
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createTopic(@NonNull String memo) throws HieroBaseException;

  /**
   * Create a new Topic.
   *
   * @param adminKey the adminKey for the topic
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createTopic(@NonNull PrivateKey adminKey, @NonNull String memo)
      throws HieroBaseException;

  /**
   * Create a new private Topic.
   *
   * @param submitKey the submitKey for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(@NonNull PrivateKey submitKey) throws HieroBaseException;

  /**
   * Create a new private Topic.
   *
   * @param adminKey the adminKey for the topic
   * @param submitKey the submitKey for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(@NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey)
      throws HieroBaseException;

  /**
   * Create a new private Topic.
   *
   * @param submitKey the submitKey for the topic
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(@NonNull PrivateKey submitKey, @NonNull String memo)
      throws HieroBaseException;

  /**
   * Create a new private Topic.
   *
   * @param adminKey the adminKey for the topic
   * @param submitKey the submitKey for the topic
   * @param memo the memo for the topic
   * @return the ID of the new Topic
   * @throws HieroBaseException if the Topic could not be created
   */
  @NonNull TopicId createPrivateTopic(
      @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey, @NonNull String memo)
      throws HieroBaseException;

  /**
   * Update a Topic. The operator account privateKey is used as adminKey for updating topic.
   *
   * @param topicId the topicId of topic to update
   * @param memo the memo for the topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateTopic(@NonNull TopicId topicId, @NonNull String memo) throws HieroBaseException;

  /**
   * Update a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey for topic
   * @param memo the memo for the topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateTopic(@NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull String memo)
      throws HieroBaseException;

  /**
   * Update a Topic. The operator account privateKey is used as adminKey for updating topic.
   *
   * @param topicId the topicId of topic to update
   * @param updatedAdminKey the new adminKey for topic
   * @param submitKey the new submit for topic
   * @param memo the memo for the topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateTopic(
      @NonNull TopicId topicId,
      @NonNull PrivateKey updatedAdminKey,
      @NonNull PrivateKey submitKey,
      @NonNull String memo)
      throws HieroBaseException;

  /**
   * Update a Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param updatedAdminKey the new adminKey for topic
   * @param submitKey the new submit for topic
   * @param memo the memo for the topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateTopic(
      @NonNull TopicId topicId,
      @NonNull PrivateKey adminKey,
      @NonNull PrivateKey updatedAdminKey,
      @NonNull PrivateKey submitKey,
      @NonNull String memo)
      throws HieroBaseException;

  /**
   * Update adminKey for Topic. The operator account privateKey is used as adminKey for updating
   * topic.
   *
   * @param topicId the topicId of topic to update
   * @param updatedAdminKey the new adminKey for topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateAdminKey(@NonNull TopicId topicId, @NonNull PrivateKey updatedAdminKey)
      throws HieroBaseException;

  /**
   * Update adminKey for Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param updatedAdminKey the new adminKey for topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateAdminKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull PrivateKey updatedAdminKey)
      throws HieroBaseException;

  /**
   * Update submitKey for Topic. The operator account privateKey is used as adminKey for updating
   * topic.
   *
   * @param topicId the topicId of topic to update
   * @param submitKey the new submitKey for topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateSubmitKey(@NonNull TopicId topicId, @NonNull PrivateKey submitKey)
      throws HieroBaseException;

  /**
   * Update submitKey for Topic.
   *
   * @param topicId the topicId of topic to update
   * @param adminKey the adminKey of topic
   * @param submitKey the new submitKey for topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void updateSubmitKey(
      @NonNull TopicId topicId, @NonNull PrivateKey adminKey, @NonNull PrivateKey submitKey)
      throws HieroBaseException;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void deleteTopic(@NonNull TopicId topicId) throws HieroBaseException;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroBaseException if the Topic could not be created
   */
  default void deleteTopic(@NonNull String topicId) throws HieroBaseException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    deleteTopic(TopicId.fromString(topicId));
  }
  ;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void deleteTopic(@NonNull TopicId topicId, @NonNull PrivateKey adminKey) throws HieroBaseException;

  /**
   * Delete a Topic.
   *
   * @param topicId the topicId of topic
   * @throws HieroBaseException if the Topic could not be created
   */
  default void deleteTopic(@NonNull String topicId, @NonNull String adminKey)
      throws HieroBaseException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(adminKey, "adminKey cannot be null");
    try {
      deleteTopic(TopicId.fromString(topicId), PrivateKey.fromString(adminKey));
    } catch (IllegalArgumentException e) {
      throw new HieroSecurityException("Invalid private key format", e);
    }
  }
  ;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void submitMessage(@NonNull TopicId topicId, @NonNull byte[] message) throws HieroBaseException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroBaseException if the Topic could not be created
   */
  default void submitMessage(@NonNull String topicId, @NonNull byte[] message)
      throws HieroBaseException {
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
   * @throws HieroBaseException if the Topic could not be created
   */
  void submitMessage(@NonNull TopicId topicId, @NonNull String message) throws HieroBaseException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroBaseException if the Topic could not be created
   */
  default void submitMessage(@NonNull String topicId, @NonNull String message)
      throws HieroBaseException {
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
   * @throws HieroBaseException if the Topic could not be created
   */
  void submitMessage(
      @NonNull TopicId topicId, @NonNull PrivateKey submitKey, @NonNull byte[] message)
      throws HieroBaseException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroBaseException if the Topic could not be created
   */
  default void submitMessage(
      @NonNull String topicId, @NonNull String submitKey, @NonNull byte[] message)
      throws HieroBaseException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(submitKey, "submitKey cannot be null");
    Objects.requireNonNull(message, "message cannot be null");
    try {
      submitMessage(TopicId.fromString(topicId), PrivateKey.fromString(submitKey), message);
    } catch (IllegalArgumentException e) {
      throw new HieroSecurityException("Invalid private key format", e);
    }
  }

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param submitKey the submit key for submitting message
   * @param message the message to send to topic
   * @throws HieroBaseException if the Topic could not be created
   */
  void submitMessage(
      @NonNull TopicId topicId, @NonNull PrivateKey submitKey, @NonNull String message)
      throws HieroBaseException;

  /**
   * Submit a message to a Topic.
   *
   * @param topicId the topicId of topic
   * @param message the message to send to topic
   * @throws HieroBaseException if the Topic could not be created
   */
  default void submitMessage(
      @NonNull String topicId, @NonNull String submitKey, @NonNull String message)
      throws HieroBaseException {
    Objects.requireNonNull(topicId, "topicId cannot be null");
    Objects.requireNonNull(submitKey, "submitKey cannot be null");
    Objects.requireNonNull(message, "message cannot be null");
    try {
      submitMessage(TopicId.fromString(topicId), PrivateKey.fromString(submitKey), message);
    } catch (IllegalArgumentException e) {
      throw new HieroSecurityException("Invalid private key format", e);
    }
  }
}
