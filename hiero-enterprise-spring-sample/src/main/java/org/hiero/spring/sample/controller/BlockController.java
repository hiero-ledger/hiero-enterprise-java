package org.hiero.spring.sample.controller;

import java.util.Objects;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Hiero block operations.
 * This controller provides endpoints for querying block information from the mirror node.
 */
@RestController
@RequestMapping("/api/v1/hiero/blocks")
public class BlockController {

  private final BlockRepository blockRepository;

  public BlockController(final BlockRepository blockRepository) {
    this.blockRepository =
        Objects.requireNonNull(blockRepository, "blockRepository must not be null");
  }

  /**
   * Retrieves a paginated list of all blocks.
   *
   * @return A page of blocks.
   */
  @GetMapping
  public Page<Block> getBlocks() {
    try {
      return blockRepository.findAll();
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query blocks", e);
    }
  }

  /**
   * Retrieves a specific block by its number.
   *
   * @param number The block number.
   * @return The block details.
   */
  @GetMapping("/{number}")
  public Block getBlockByNumber(@PathVariable("number") final long number) {
    try {
      return blockRepository.findByNumber(number)
          .orElseThrow(() -> new RuntimeException("Block not found: " + number));
    } catch (final Exception e) {
      throw new RuntimeException("Failed to query block by number: " + number, e);
    }
  }
}
