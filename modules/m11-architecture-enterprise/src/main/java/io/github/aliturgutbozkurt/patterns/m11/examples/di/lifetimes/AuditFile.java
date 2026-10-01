package io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes;

import java.time.Clock;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An {@link AutoCloseable} resource with application lifetime. It writes to an injected "disk" so the demo and the
 * tests can see every line, including the one written when it is closed.
 *
 * @see "m11 lesson, section Dependency Injection — lifetimes"
 */
public final class AuditFile implements AutoCloseable {

    private final String name;
    private final Clock clock;
    private final Consumer<String> disk;
    private boolean closed;

    public AuditFile(String name, Clock clock, Consumer<String> disk) {
        this.name = Objects.requireNonNull(name, "name");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.disk = Objects.requireNonNull(disk, "disk");
    }

    /** Appends a time-stamped line; the time comes from the injected clock. */
    public void write(String line) {
        if (closed) {
            throw new IllegalStateException(name + " is closed");
        }
        disk.accept(name + " " + clock.instant() + " " + line);
    }

    /** Closes the file once; later calls do nothing. */
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            disk.accept(name + " closed");
        }
    }
}
