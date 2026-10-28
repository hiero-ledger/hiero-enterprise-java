package org.hiero.base.protocol.data;

import com.hedera.hashgraph.sdk.SubscriptionHandle;
import org.jspecify.annotations.NonNull;

public record TopicMessageResult(@NonNull SubscriptionHandle subscriptionHandle) {}
