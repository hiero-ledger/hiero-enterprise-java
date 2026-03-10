package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Balance;
import org.hiero.base.data.Page;
import org.hiero.base.data.Token;
import org.hiero.base.data.TokenInfo;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.TokenRepository;
import org.jspecify.annotations.NonNull;

public class TokenRepositoryImpl implements TokenRepository {
  private final MirrorNodeClient mirrorNodeClient;

  public TokenRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public Page<Token> findByAccount(@NonNull AccountId accountId) throws HieroException {
    return mirrorNodeClient.queryTokensForAccount(accountId);
  }

  @Override
  public Optional<TokenInfo> findById(@NonNull TokenId tokenId) throws HieroException {
    return mirrorNodeClient.queryTokenById(tokenId);
  }

  @Override
  public Page<Balance> getBalances(@NonNull TokenId tokenId) throws HieroException {
    return mirrorNodeClient.queryTokenBalances(tokenId);
  }

  @Override
  public Page<Balance> getBalancesForAccount(@NonNull TokenId tokenId, @NonNull AccountId accountId)
      throws HieroException {
    return mirrorNodeClient.queryTokenBalancesForAccount(tokenId, accountId);
  }
}
