package io.github.aliturgutbozkurt.patterns.m10.examples.structured.mirrors;

import java.util.Objects;

/**
 * One download mirror: a name and a blocking way to fetch a file from it.
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public record Mirror(String name, Source source) {

    /** Fetches {@code file}; may block and may fail. */
    @FunctionalInterface
    public interface Source {
        String fetch(String file) throws Exception;
    }

    public Mirror {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
    }
}
