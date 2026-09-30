package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs;

/**
 * The level of a log line. A line that does not start with a known level is {@link #UNPARSEABLE}: bad input is
 * counted, never thrown, so one broken line cannot kill a consumer.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public enum Level {
    ERROR, WARN, INFO, DEBUG, UNPARSEABLE;

    /** The level of a line such as {@code "WARN disk 91% full"}. */
    public static Level of(String line) {
        String first = line.split(" ", 2)[0];
        return switch (first) {
            case "ERROR" -> ERROR;
            case "WARN" -> WARN;
            case "INFO" -> INFO;
            case "DEBUG" -> DEBUG;
            default -> UNPARSEABLE;
        };
    }
}
