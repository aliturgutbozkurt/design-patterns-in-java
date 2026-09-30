package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before;

/**
 * ANTI-PATTERN — patternitis: the only concrete greeter, spread over two classes and an interface.
 *
 * @see "m11 lesson, section Anti-patterns — patternitis"
 */
public final class FormalGreeter extends AbstractGreeter {

    @Override
    protected String prefix() {
        return "Good day, ";
    }

    @Override
    protected String suffix() {
        return ".";
    }
}
