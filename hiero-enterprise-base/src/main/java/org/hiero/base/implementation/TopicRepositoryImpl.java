package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.TopicId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Page;
import org.hiero.base.data.Topic;
import org.hiero.base.data.TopicMessage;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.TopicRepository;
import org.jspecify.annotations.NonNull;

public class TopicRepositoryImpl implements TopicRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public TopicRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public @NonNull Optional<Topic> findTopicById(TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    return mirrorNodeClient.queryTopicById(topicId);
  }

  @Override
  public @NonNull Page<TopicMessage> getMessages(TopicId topicId) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    return mirrorNodeClient.queryTopicMessages(topicId);
  }

  @Override
  public @NonNull Optional<TopicMessage> getMessageBySequenceNumber(
      TopicId topicId, long sequenceNumber) throws HieroException {
    Objects.requireNonNull(topicId, "topicId must not be null");
    if (sequenceNumber < 1) {
      throw new IllegalArgumentException("sequenceNumber must be greater than 0");
    }
    return mirrorNodeClient.queryTopicMessageBySequenceNumber(topicId, sequenceNumber);
  }
}
