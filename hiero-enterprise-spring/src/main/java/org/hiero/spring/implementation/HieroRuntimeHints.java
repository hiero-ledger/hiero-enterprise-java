package org.hiero.spring.implementation;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Runtime hints for GraalVM Native Image support in Hiero Enterprise Spring.
 */
public class HieroRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register configuration properties for reflection
        hints.reflection().registerType(HieroProperties.class);
        hints.reflection().registerType(HieroNetworkProperties.class);
        hints.reflection().registerType(HieroNode.class);
    }
}
