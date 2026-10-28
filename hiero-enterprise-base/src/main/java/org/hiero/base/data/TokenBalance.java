package org.hiero.base.data;

import com.hedera.hashgraph.sdk.TokenId;
import org.jspecify.annotations.Nullable;

/**
 * Represents the balance of a token held by an account.
 *
 * @param tokenId the network entity ID of the token
 * @param balance the token balance in the token's smallest denomination
 */
public record TokenBalance(@Nullable TokenId tokenId, long balance) {}
