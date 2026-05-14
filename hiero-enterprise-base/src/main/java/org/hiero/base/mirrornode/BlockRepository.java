package org.hiero.base.mirrornode;

import java.util.Optional;
import org.hiero.base.HieroBaseException;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.jspecify.annotations.NonNull;

/** Repository for querying Blocks from the Mirror Node. */
public interface BlockRepository {

  /**
   * Queries all blocks.
   *
   * @return a page of blocks
   * @throws HieroBaseException if an error occurs
   */
  @NonNull Page<Block> findAll() throws HieroBaseException;

  /**
   * Queries a block by its number.
   *
   * @param number the block number
   * @return the block, or empty if not found
   * @throws HieroBaseException if an error occurs
   */
  @NonNull Optional<Block> findByNumber(long number) throws HieroBaseException;

  /**
   * Queries a block by its hash.
   *
   * @param hash the block hash
   * @return the block, or empty if not found
   * @throws HieroBaseException if an error occurs
   */
  @NonNull Optional<Block> findByHash(@NonNull String hash) throws HieroBaseException;
}
