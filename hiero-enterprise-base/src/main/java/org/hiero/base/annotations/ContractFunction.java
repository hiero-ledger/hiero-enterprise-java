package org.hiero.base.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map a Java method to a Hiero Smart Contract function.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ContractFunction {
    /**
     * @return the name of the function in Solidity. Defaults to the Java method name.
     */
    String value() default "";
}
