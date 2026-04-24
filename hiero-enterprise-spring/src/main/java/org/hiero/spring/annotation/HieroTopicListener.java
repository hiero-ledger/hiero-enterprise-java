package org.hiero.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as a listener for Hiero Consensus Service (HCS) messages.
 *
 * <p>The annotated method should accept a single parameter of type {@code TopicMessage}.
 *
 * <pre>{@code
 * @HieroTopicListener(topicId = "0.0.5678")
 * public void onMessage(TopicMessage msg) {
 *     log.info("Received message: {}", msg.message());
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HieroTopicListener {

    /**
     * The topic ID to monitor for messages.
     *
     * @return the topic ID (e.g., "0.0.5678").
     */
    String topicId();

    /**
     * Polling interval in milliseconds. Defaults to 5000ms (5 seconds).
     *
     * @return the polling interval.
     */
    long interval() default 5000;
}
