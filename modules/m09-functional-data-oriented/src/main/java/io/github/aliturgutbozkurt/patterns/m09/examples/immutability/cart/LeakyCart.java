package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.cart;

import java.util.List;

/**
 * The pitfall: a record that keeps the caller's list. Its field is final, but the list it points to is not, so the
 * record is only <em>shallowly</em> immutable.
 *
 * @see "m09 lesson, section Immutability and value objects"
 */
public record LeakyCart(List<CartLine> lines) {}
