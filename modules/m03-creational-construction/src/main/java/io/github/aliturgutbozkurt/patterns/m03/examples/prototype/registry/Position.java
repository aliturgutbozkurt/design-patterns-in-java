package io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry;

/**
 * A map position (immutable value; the unit's <em>field</em> holding it is what changes).
 *
 * @see "m03 lesson, section Prototype"
 */
public record Position(int x, int y) {}
