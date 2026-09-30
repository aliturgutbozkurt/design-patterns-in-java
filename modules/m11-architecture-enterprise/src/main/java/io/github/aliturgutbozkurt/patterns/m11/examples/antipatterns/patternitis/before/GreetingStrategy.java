package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before;

/**
 * ANTI-PATTERN — patternitis: a Strategy interface with exactly one implementation, "in case we need another one".
 *
 * @see "m11 lesson, section Anti-patterns — patternitis"
 */
public interface GreetingStrategy {

    String greet(String name);
}
