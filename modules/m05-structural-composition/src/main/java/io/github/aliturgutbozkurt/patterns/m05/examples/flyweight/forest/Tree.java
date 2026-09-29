package io.github.aliturgutbozkurt.patterns.m05.examples.flyweight.forest;

/**
 * Context object: the <em>extrinsic</em> state of one tree (its position) plus a reference to a shared
 * {@link TreeType}.
 *
 * @see "m05 lesson, section Flyweight"
 */
public record Tree(int x, int y, TreeType type) {}
