package io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes;

import java.util.Objects;

/**
 * Transient collaborator: cheap and stateless apart from its number, so the root creates a new one on every lookup.
 *
 * @see "m11 lesson, section Dependency Injection — lifetimes"
 */
public final class RequestLog {

    private final int number;
    private final AuditFile file;

    public RequestLog(int number, AuditFile file) {
        this.number = number;
        this.file = Objects.requireNonNull(file, "file");
    }

    /** Which lookup created this log (1, 2, …). */
    public int number() {
        return number;
    }

    /** Writes {@code message} to the access audit file. */
    public void record(String message) {
        file.write("log " + number + ": " + message);
    }
}
