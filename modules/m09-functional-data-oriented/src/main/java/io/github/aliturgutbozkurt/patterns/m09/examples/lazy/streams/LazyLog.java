package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.streams;

import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A tiny logger with the same deferred-message method as {@link System.Logger#log(Level, Supplier)}: the message
 * is built only if its level is enabled, so a disabled debug line costs nothing.
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class LazyLog {

    private final Level threshold;
    private final Consumer<String> sink;

    public LazyLog(Level threshold, Consumer<String> sink) {
        this.threshold = Objects.requireNonNull(threshold, "threshold");
        this.sink = Objects.requireNonNull(sink, "sink");
    }

    public boolean isEnabled(Level level) {
        return level.getSeverity() >= threshold.getSeverity();
    }

    public void log(Level level, Supplier<String> message) {
        if (isEnabled(level)) {
            sink.accept(level.getName() + " " + message.get());
        }
    }
}
