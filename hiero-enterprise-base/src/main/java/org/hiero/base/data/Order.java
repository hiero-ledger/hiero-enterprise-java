package org.hiero.base.data;

/** Enum for sort order. */
public enum Order {
  ASC,
  DESC;

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
