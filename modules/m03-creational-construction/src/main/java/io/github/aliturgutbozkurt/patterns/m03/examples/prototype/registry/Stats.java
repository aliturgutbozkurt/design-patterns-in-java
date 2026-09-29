package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry;

/**
 * Immutable unit statistics — safe to share between every copy, so prototypes never copy them.
 *
 * @see "m03 lesson, section Prototype"
 */
public record Stats(int health, int attack, int range) {}
