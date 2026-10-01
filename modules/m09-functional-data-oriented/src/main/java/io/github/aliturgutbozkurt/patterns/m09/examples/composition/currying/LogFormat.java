package io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying;

import java.util.Locale;
import java.util.function.Function;

/**
 * Log lines as a curried function: level → component → message → line. Each step returns a more specialised
 * formatter.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class LogFormat {

    /**
     * Severity of a log line.
     *
     * @see "m09 lesson, section Function composition, currying and partial application"
     */
    public enum Level { INFO, WARN, ERROR }

    private LogFormat() {}

    public static Function<Level, Function<String, Function<String, String>>> curried() {
        return level -> component -> message ->
                String.format(Locale.ROOT, "[%-5s] %s: %s", level, component, message);
    }

    /** A formatter with the level already fixed. */
    public static Function<String, Function<String, String>> of(Level level) {
        return curried().apply(level);
    }
}
