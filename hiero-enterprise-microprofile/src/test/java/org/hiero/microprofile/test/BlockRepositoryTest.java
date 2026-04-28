package org.hiero.microprofile.test;

import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.HieroException;
import org.hiero.base.data.Block;
import org.hiero.base.data.Page;
import org.hiero.base.mirrornode.BlockRepository;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class BlockRepositoryTest {

  @Inject private BlockRepository blockRepository;

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

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
