package com.openelements.hiero.smartcontract.abi.util;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.jspecify.annotations.NonNull;

/** Utility class for Keccak hashing. */
public class KeccakUtil {

  /**
   * Computes the Keccak-256 hash of the given data.
   *
   * @param data the data to hash
   * @return the Keccak-256 hash of the data
   */
  @NonNull
  public static byte[] keccak256(@NonNull final byte[] data) {
    final Keccak.DigestKeccak kecc = new Keccak.Digest256();
    kecc.update(data);
    return kecc.digest();
  }
}
