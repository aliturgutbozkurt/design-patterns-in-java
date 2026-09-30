package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before;

/**
 * ANTI-PATTERN — see lesson: a plain value, but looked up through the {@link ServiceLocator} instead of passed in.
 *
 * @param minimumUnits reorder when stock falls below this
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public record ReorderPolicy(int minimumUnits) {}
