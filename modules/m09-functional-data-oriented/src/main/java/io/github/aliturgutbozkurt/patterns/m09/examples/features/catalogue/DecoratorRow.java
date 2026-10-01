package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.Locale;
import java.util.function.Function;

/**
 * Catalogue row: Decorator. Wrapper classes around a shared interface became functions joined with
 * {@link Function#andThen}.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class DecoratorRow {

    static final String INPUT = "  spring sale  ";

    private DecoratorRow() {}

    /**
     * Before: each decoration is a class that wraps another {@code Text}.
     *
     * @see "m09 lesson, section Patterns that became language features"
     */
    public static final class Classic {

        interface Text {
            String render();
        }

        record Plain(String value) implements Text {
            public String render() { return value; }
        }

        record Trimmed(Text inner) implements Text {
            public String render() { return inner.render().strip(); }
        }

        record Upper(Text inner) implements Text {
            public String render() { return inner.render().toUpperCase(Locale.ROOT); }
        }

        record Bracketed(Text inner) implements Text {
            public String render() { return "[" + inner.render() + "]"; }
        }

        private Classic() {}

        public static String run() {
            return new Bracketed(new Upper(new Trimmed(new Plain(INPUT)))).render();
        }
    }

    /**
     * After: each decoration is a function; the stack is a composition.
     *
     * @see "m09 lesson, section Patterns that became language features"
     */
    public static final class Modern {

        static final Function<String, String> TRIM = String::strip;
        static final Function<String, String> UPPER = s -> s.toUpperCase(Locale.ROOT);
        static final Function<String, String> BRACKET = s -> "[" + s + "]";

        private Modern() {}

        public static String run() {
            return TRIM.andThen(UPPER).andThen(BRACKET).apply(INPUT);
        }
    }
}
