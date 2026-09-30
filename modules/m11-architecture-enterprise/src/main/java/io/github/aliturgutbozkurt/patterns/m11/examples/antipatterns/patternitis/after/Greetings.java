package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.patternitis.after;

import java.util.function.Function;

/**
 * The same behaviour without speculative generality: one static method, and one function for the only thing that
 * might ever vary. Introduce a pattern when the second variation arrives, not before.
 *
 * @see "m11 lesson, section Anti-patterns — patternitis"
 */
public final class Greetings {

    private static final Function<String, String> FORMAL = name -> "Good day, " + name + ".";

    private Greetings() {}

    public static String greet(String name) {
        return FORMAL.apply(name == null || name.isBlank() ? "guest" : name.strip());
    }
}
