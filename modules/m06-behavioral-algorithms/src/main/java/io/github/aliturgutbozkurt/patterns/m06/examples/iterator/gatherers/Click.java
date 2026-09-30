package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.gatherers;

import java.util.Objects;

/**
 * A page view in a web analytics stream, {@code second} seconds after the visit started.
 *
 * @see "m06 lesson, section Iterator"
 */
public record Click(String page, long second) {

    public Click {
        Objects.requireNonNull(page, "page");
    }

    /** E.g. {@code "home@0"}. */
    @Override
    public String toString() {
        return page + "@" + second;
    }
}
