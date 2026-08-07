package org.hiero.microprofile.test;

import com.hedera.hashgraph.sdk.FileId;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.hiero.base.FileClient;
import org.hiero.base.HieroException;
import org.hiero.base.protocol.data.FileCreateRequest;
import org.hiero.microprofile.ClientProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(ClientProvider.class)
@Configuration(useExisting = true)
public class FileClientTests {

  @BeforeAll
  static void setup() {
    final Config build =
        ConfigProviderResolver.instance().getBuilder().withSources(new TestConfigSource()).build();
    ConfigProviderResolver.instance()
        .registerConfig(build, Thread.currentThread().getContextClassLoader());
  }

  @Inject private FileClient fileClient;

  @Test
  void testFileClient() throws Exception {
    // given
    final byte[] contents = "Hello, Hedera!".getBytes();
    Assertions.assertNotNull(fileClient);

    // when
    final FileId fileId = fileClient.createFile(contents);

    // then
    Assertions.assertNotNull(fileId);
  }

  @Test
  void testAppendFile() throws HieroException {
    final byte[] contents = "Hello,".getBytes();
    final FileId fileId = fileClient.createFile(contents);

    Assertions.assertNotNull(fileId);

    fileClient.appendFile(fileId, " Hiero!");
    byte[] bytes = fileClient.readFile(fileId);

    Assertions.assertNotNull(bytes);
    Assertions.assertEquals("Hello, Hiero!", new String(bytes));
  }

  @Test
  void testAppendFileThrowsExceptionWhenContentExceedMaxSize() throws HieroException {
    final byte[] content = new byte[FileCreateRequest.FILE_MAX_SIZE + 1];
    final FileId fileId = fileClient.createFile(new byte[0]);

    Assertions.assertNotNull(fileId);
    Assertions.assertThrows(HieroException.class, () -> fileClient.appendFile(fileId, content));
  }

  @Test
  void testChunkedAppendFile() throws HieroException {
    final String content = "A".repeat(FileCreateRequest.FILE_CREATE_MAX_SIZE * 2);
    final FileId fileId = fileClient.createFile(new byte[0]);

    Assertions.assertNotNull(fileId);

    fileClient.appendFile(fileId, content);
    byte[] bytes = fileClient.readFile(fileId);

    Assertions.assertNotNull(bytes);
    Assertions.assertEquals(content, new String(bytes));
  }
}
