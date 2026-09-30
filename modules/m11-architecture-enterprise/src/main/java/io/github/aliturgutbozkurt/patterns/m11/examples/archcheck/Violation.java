package io.github.aliturgutbozkurt.patterns.m11.examples.archcheck;

import java.util.Objects;

/**
 * One forbidden dependency.
 *
 * @param from the class that depends
 * @param to the class it must not depend on
 * @see "m11 lesson, section Architecture rules — how the tools work"
 */
public record Violation(String from, String to) implements Comparable<Violation> {

    public Violation {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
    }

    @Override
    public int compareTo(Violation other) {
        int byFrom = from.compareTo(other.from);
        return byFrom != 0 ? byFrom : to.compareTo(other.to);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
