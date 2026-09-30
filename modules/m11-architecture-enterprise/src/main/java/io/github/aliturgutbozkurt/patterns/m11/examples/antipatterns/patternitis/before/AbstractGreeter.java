package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before;

/**
 * ANTI-PATTERN — patternitis: a Template Method base class for a single subclass.
 *
 * @see "m11 lesson, section Anti-patterns — patternitis"
 */
public abstract class AbstractGreeter implements GreetingStrategy {

    @Override
    public final String greet(String name) {
        String normalized = name == null || name.isBlank() ? "guest" : name.strip();
        return prefix() + normalized + suffix();
    }

    protected abstract String prefix();

    protected abstract String suffix();
}
