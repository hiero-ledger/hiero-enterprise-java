package org.hiero.spring.implementation;

/**
 * Configuration properties for Hiero health indicators.
 */
public class HieroHealthProperties {

  /**
   * Whether to enable Hiero health indicators.
   */
  private boolean enabled = true;

  /**
   * Minimum balance for the operator account (in Hbar) before reporting a warning/error.
   */
  private long minBalanceInHbar = 10;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public long getMinBalanceInHbar() {
    return minBalanceInHbar;
  }

  public void setMinBalanceInHbar(long minBalanceInHbar) {
    this.minBalanceInHbar = minBalanceInHbar;
  }
}
