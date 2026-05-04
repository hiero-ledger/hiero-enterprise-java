package org.hiero.test;

import com.hedera.hashgraph.sdk.ContractId;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.hiero.base.HieroException;
import org.hiero.base.SmartContractClient;
import org.jspecify.annotations.NonNull;

/**
 * Utility class to deploy test contracts.
 */
public class ContractDeploymentHelper {

  private final SmartContractClient contractClient;

  public ContractDeploymentHelper(@NonNull SmartContractClient contractClient) {
    this.contractClient = contractClient;
  }

  /**
   * Deploys the TestEventContract.
   *
   * @return the deployed contract ID
   * @throws HieroException if deployment fails
   */
  public ContractId deployTestEventContract() throws HieroException {
    try {
      byte[] bytecode = loadBytecode("/contracts/TestEventContract.bin");
      return contractClient.createContract(bytecode);
    } catch (IOException e) {
      throw new HieroException("Failed to load TestEventContract bytecode", e);
    }
  }

  private byte[] loadBytecode(String path) throws IOException {
    try (InputStream is = getClass().getResourceAsStream(path)) {
      if (is == null) {
        throw new IOException("Resource not found: " + path);
      }
      String hex = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
      return hexToBytes(hex);
    }
  }

  private byte[] hexToBytes(String hex) {
    hex = hex.replace("0x", "");
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }
}
