package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after;

/**
 * Where order numbers come from — an abstraction the service asks for instead of reaching for a global.
 *
 * @see "m02 lesson, section Singleton — why it is often an anti-pattern"
 */
@FunctionalInterface
public interface IdSource {

    int nextId();
}
