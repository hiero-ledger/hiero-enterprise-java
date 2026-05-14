package org.hiero.base.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark an interface as a Hiero Smart Contract proxy.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SmartContract {
    /**
     * @return the ID of the contract (e.g. "0.0.12345")
     */
    String value() default "";
}
