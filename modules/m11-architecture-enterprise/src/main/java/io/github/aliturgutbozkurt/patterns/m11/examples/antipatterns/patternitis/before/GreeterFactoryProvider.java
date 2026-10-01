package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before;

/**
 * ANTI-PATTERN — patternitis: a factory for the factory. Five types to say "Good day, Ada."
 *
 * @see "m11 lesson, section Anti-patterns — patternitis"
 */
public final class GreeterFactoryProvider {

    private GreeterFactoryProvider() {}

    public static GreeterFactory defaultFactory() {
        return new GreeterFactory();
    }
}
