package org.hiero.spring.test;

import java.util.Optional;
import org.hiero.base.HieroException;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HieroTestConfig.class)
public class BlockRepositoryTest {

  @Autowired private BlockRepository blockRepository;

  @Test
  void testNullParam() {
    Assertions.assertThrows(NullPointerException.class, () -> blockRepository.findByHash(null));
  }

  @Test
  void testFindAll() throws HieroException {
    // when
    final Page<Block> blocks = blockRepository.findAll();

    // then
    Assertions.assertNotNull(blocks);
    Assertions.assertNotNull(blocks.getData());
  }

  @Test
  void testFindByNumberWithNonExistentBlock() throws HieroException {
    // given
    final long nonExistentBlockNumber = 999999999L;

    // when
    final Optional<Block> result = blockRepository.findByNumber(nonExistentBlockNumber);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  void testFindByHashWithNonExistentBlock() throws HieroException {
    // given
    final String nonExistentBlockHash =
        "0x0000000000000000000000000000000000000000000000000000000000000000";

    // when
    final Optional<Block> result = blockRepository.findByHash(nonExistentBlockHash);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isPresent());
  }
}
