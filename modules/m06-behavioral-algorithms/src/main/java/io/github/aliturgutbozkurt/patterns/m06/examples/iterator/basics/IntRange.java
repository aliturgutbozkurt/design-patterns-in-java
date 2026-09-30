package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.basics;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The canonical Iterator: the numbers {@code start, start + step, ...} below {@code end}, computed on demand. Being
 * {@link Iterable} is what makes the for-each loop work.
 *
 * @see "m06 lesson, section Iterator"
 */
public record IntRange(int start, int end, int step) implements Iterable<Integer> {

    public IntRange {
        if (step <= 0) {
            throw new IllegalArgumentException("step must be > 0: " + step);
        }
    }

    /** Every iterator has its own cursor, so two loops over the same range do not disturb each other. */
    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private long next = start;  // long: next + step must not overflow near Integer.MAX_VALUE

            @Override
            public boolean hasNext() {
                return next < end;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("range exhausted at " + next);
                }
                int value = (int) next;
                next += step;
                return value;
            }
        };
    }

    /** How many numbers the range yields. */
    public int size() {
        return start >= end ? 0 : (int) (((long) end - start + step - 1) / step);
    }

    /** The default {@code Iterable.spliterator()} knows neither size nor order; this one reports both. */
    @Override
    public Spliterator<Integer> spliterator() {
        return Spliterators.spliterator(iterator(), size(),
                Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE);
    }

    public Stream<Integer> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
