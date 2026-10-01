package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.before;

/**
 * ANTI-PATTERN — patternitis: a factory that can only ever build one thing.
 *
 * @see "m11 lesson, section Anti-patterns — patternitis"
 */
public class GreeterFactory {

    public GreetingStrategy create() {
        return new FormalGreeter();
    }
}
