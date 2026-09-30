package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface method that changes state, so a read-only proxy can block it.
 *
 * @see "m04 lesson, section Proxy"
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)                          // must survive to run time: the handler reads it
@Target(ElementType.METHOD)
public @interface Mutator {}
