package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.after;

/**
 * An ordinary class: create one and share it if you want "a single instance" — that is now a wiring decision.
 *
 * @see "m02 lesson, section Singleton — why it is often an anti-pattern"
 */
public final class SequentialIds implements IdSource {

    private int next = 1;

    @Override
    public synchronized int nextId() {
        return next++;
    }
}
