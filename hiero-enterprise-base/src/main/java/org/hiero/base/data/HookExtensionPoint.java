package org.hiero.base.data;

/** Hook extension points supported by account hook creation. */
public enum HookExtensionPoint {
  ACCOUNT_ALLOWANCE_HOOK;

  public com.hedera.hashgraph.sdk.HookExtensionPoint toHederaSdkType() {
    return com.hedera.hashgraph.sdk.HookExtensionPoint.ACCOUNT_ALLOWANCE_HOOK;
  }
}
