package org.hiero.base.data;

import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record TokenBalance(@NonNull TokenId tokenId, long balance) {
  public TokenBalance {
    Objects.requireNonNull(tokenId, "tokenId must not be null");
  }
}
