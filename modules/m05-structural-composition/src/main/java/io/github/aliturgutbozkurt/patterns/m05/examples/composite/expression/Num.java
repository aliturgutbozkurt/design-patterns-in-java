package io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression;

/**
 * Leaf: a whole number.
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public record Num(long value) implements Expr {}
