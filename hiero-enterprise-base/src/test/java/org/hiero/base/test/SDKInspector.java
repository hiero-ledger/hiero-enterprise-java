package org.hiero.base.test;

import com.hedera.hashgraph.sdk.TransactionRecord;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SDKInspector {
  public static void main(String[] args) {
    System.out.println("Methods of TransactionRecord:");
    for (Method m : TransactionRecord.class.getMethods()) {
      System.out.println(m.getName());
    }
    System.out.println("Fields of TransactionRecord:");
    for (Field f : TransactionRecord.class.getFields()) {
      System.out.println(f.getName());
    }
  }
}
