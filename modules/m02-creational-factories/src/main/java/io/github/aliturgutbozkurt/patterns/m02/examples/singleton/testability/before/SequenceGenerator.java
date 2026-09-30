package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.before;

/**
 * A classic Singleton holding mutable state: every caller in the JVM shares one counter, and nothing can reset it.
 *
 * @see "m02 lesson, section Singleton — why it is often an anti-pattern"
 */
public final class SequenceGenerator {

    private static final SequenceGenerator INSTANCE = new SequenceGenerator();

    // Mutable state behind a global access point — the problem this example demonstrates.
    private int next = 1;

    private SequenceGenerator() {}

    public static SequenceGenerator getInstance() {
        return INSTANCE;
    }

    public synchronized int next() {
        return next++;
    }
}
