package org.hiero.base.data;

/** Represents an active subscription to event source. */
public interface Subscription {
  /** Cancels this subscription. */
  void unsubscribe();
}
