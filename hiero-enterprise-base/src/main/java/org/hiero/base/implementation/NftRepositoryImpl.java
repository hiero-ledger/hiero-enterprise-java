package org.hiero.base.implementation;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.TokenId;
import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroBaseException;
import org.hiero.base.data.Nft;
import org.hiero.base.data.NftMetadata;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.hiero.base.mirrornode.NftRepository;
import org.jspecify.annotations.NonNull;

public class NftRepositoryImpl implements NftRepository {

  private final MirrorNodeClient mirrorNodeClient;

  public NftRepositoryImpl(@NonNull final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @NonNull
  @Override
  public Page<Nft> findByOwner(@NonNull final AccountId owner) throws HieroBaseException {
    return mirrorNodeClient.queryNftsByAccount(owner);
  }

  @NonNull
  @Override
  public Page<Nft> findByType(@NonNull final TokenId tokenId) throws HieroBaseException {
    return mirrorNodeClient.queryNftsByTokenId(tokenId);
  }

  @NonNull
  @Override
  public Optional<Nft> findByTypeAndSerial(@NonNull final TokenId tokenId, final long serialNumber)
      throws HieroBaseException {
    return mirrorNodeClient.queryNftsByTokenIdAndSerial(tokenId, serialNumber);
  }

  @NonNull
  @Override
  public Page<Nft> findByOwnerAndType(
      @NonNull final AccountId owner, @NonNull final TokenId tokenId) throws HieroBaseException {
    return mirrorNodeClient.queryNftsByAccountAndTokenId(owner, tokenId);
  }

  @NonNull
  @Override
  public Optional<Nft> findByOwnerAndTypeAndSerial(
      @NonNull final AccountId owner, @NonNull final TokenId tokenId, final long serialNumber)
      throws HieroBaseException {
    return mirrorNodeClient.queryNftsByAccountAndTokenIdAndSerial(owner, tokenId, serialNumber);
  }

  @NonNull
  @Override
  public Optional<NftMetadata> getNftMetadata(TokenId tokenId) throws HieroBaseException {
    return mirrorNodeClient.getNftMetadata(tokenId);
  }

  @NonNull
  @Override
  public Page<NftMetadata> findTypesByOwner(@NonNull AccountId ownerId) throws HieroBaseException {
    return mirrorNodeClient.findNftTypesByOwner(ownerId);
  }

  @Override
  public Page<NftMetadata> findAllTypes() throws HieroBaseException {
    return mirrorNodeClient.findAllNftTypes();
  }
}
