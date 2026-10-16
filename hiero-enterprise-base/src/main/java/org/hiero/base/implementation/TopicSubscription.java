package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.SubscriptionHandle;
import org.hiero.base.data.Subscription;
import org.jspecify.annotations.NonNull;

/** Represents a subscription to a topic. */
public final class TopicSubscription implements Subscription {
  private final @NonNull SubscriptionHandle handler;

  TopicSubscription(final @NonNull SubscriptionHandle handler) {
    this.handler = handler;
  }

  @Override
  public void unsubscribe() {
    handler.unsubscribe();
  }
}
