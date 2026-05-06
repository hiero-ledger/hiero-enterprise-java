package org.hiero.base.data;

import org.jspecify.annotations.Nullable;

public record ServiceEndpoint(@Nullable String ipAddressV4, @Nullable String domainName, int port) {
  public ServiceEndpoint {}
}
