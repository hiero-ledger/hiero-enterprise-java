package org.hiero.spring;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hiero.spring.implementation.HieroAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Enables Hiero support.
 *
 * <p>Using this annotation will import the necessary configuration to enable Hiero support.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(HieroAutoConfiguration.class)
@Documented
public @interface EnableHiero {}
