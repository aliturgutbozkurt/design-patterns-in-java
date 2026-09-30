package io.github.aliturgutbozkurt.patterns.m05.examples.composite.expression;

/**
 * Composite of arithmetic: numbers are the leaves, operators are the composites; the same tree gives a value and a
 * text (see {@link Expressions}).
 *
 * @see "m05 lesson, section Composite — modern Java 27"
 */
public sealed interface Expr permits Num, Add, Mul, Neg {}
