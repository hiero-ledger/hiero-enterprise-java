package org.hiero.base.test;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import java.math.BigInteger;
import org.hiero.base.data.ContractParam;
import org.junit.jupiter.api.Assertions;

public class ContractParamFuzzTests {

  @FuzzTest
  void fuzzAddressStrings(FuzzedDataProvider data) {
    String value = data.consumeString(256);
    try {
      ContractParam.address(value);
    } catch (IllegalArgumentException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during address parsing", e);
    }
  }

  @FuzzTest
  void fuzzBytes32String(FuzzedDataProvider data) {
    String value = data.consumeString(128);
    try {
      ContractParam.bytes32(value);
    } catch (IllegalArgumentException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during bytes32 string validation", e);
    }
  }

  @FuzzTest
  void fuzzBytes32Bytes(FuzzedDataProvider data) {
    byte[] value = data.consumeBytes(64);
    try {
      ContractParam.bytes32(value);
    } catch (IllegalArgumentException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during bytes32 byte validation", e);
    }
  }

  @FuzzTest
  void fuzzLongNumericRange(FuzzedDataProvider data) {
    int choice = data.consumeInt(0, 2);
    try {
      switch (choice) {
        case 0 -> ContractParam.uint8((short) data.consumeInt(Short.MIN_VALUE, Short.MAX_VALUE));
        case 1 -> ContractParam.uint16(data.consumeInt());
        default -> ContractParam.uint32(data.consumeLong());
      }
    } catch (IllegalArgumentException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during long range validation", e);
    }
  }

  @FuzzTest
  void fuzzBigIntegerNumericRange(FuzzedDataProvider data) {
    BigInteger value = new BigInteger(data.consumeRemainingAsBytes());
    try {
      if (data.consumeBoolean()) {
        ContractParam.int72(value);
      } else {
        ContractParam.uint72(value);
      }
    } catch (IllegalArgumentException e) {
      return;
    } catch (Exception e) {
      Assertions.fail("Unexpected exception during BigInteger range validation", e);
    }
  }
}
