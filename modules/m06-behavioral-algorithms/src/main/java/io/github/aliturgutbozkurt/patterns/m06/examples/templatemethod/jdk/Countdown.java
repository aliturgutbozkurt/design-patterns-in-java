package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.jdk;

import java.util.AbstractList;
import java.util.Objects;

/**
 * The JDK's {@link AbstractList} is a template method class: implement {@code get} and {@code size}, and
 * {@code iterator}, {@code contains}, {@code indexOf}, {@code subList}, {@code equals} and {@code toString} come for free.
 * This list is {@code from, from - 1, ..., 1}.
 *
 * @see "m06 lesson, section Template Method"
 */
public final class Countdown extends AbstractList<Integer> {

    private final int from;

    public Countdown(int from) {
        if (from < 0) {
            throw new IllegalArgumentException("from must be >= 0: " + from);
        }
        this.from = from;
    }

    @Override
    public Integer get(int index) {
        Objects.checkIndex(index, from);
        return from - index;
    }

    @Override
    public int size() {
        return from;
    }
}
