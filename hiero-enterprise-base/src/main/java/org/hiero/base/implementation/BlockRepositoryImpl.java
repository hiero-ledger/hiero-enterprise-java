package org.hiero.base.implementation;

import java.util.Objects;
import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.base.mirrornode.MirrorNodeClient;
import org.jspecify.annotations.NonNull;

public class BlockRepositoryImpl implements BlockRepository {

  private final MirrorNodeClient mirrorNodeClient;

  public BlockRepositoryImpl(final MirrorNodeClient mirrorNodeClient) {
    this.mirrorNodeClient =
        Objects.requireNonNull(mirrorNodeClient, "mirrorNodeClient must not be null");
  }

  @Override
  public @NonNull Page<Block> findAll() throws HieroException {
    return mirrorNodeClient.queryBlocks();
  }

  @Override
  public @NonNull Optional<Block> findByNumber(long number) throws HieroException {
    return mirrorNodeClient.queryBlockByNumber(number);
  }

  @Override
  public @NonNull Optional<Block> findByHash(@NonNull String hash) throws HieroException {
    Objects.requireNonNull(hash, "hash must not be null");
    return mirrorNodeClient.queryBlockByHash(hash);
  }
}
