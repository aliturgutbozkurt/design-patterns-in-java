package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * Catalogue row: Iterator. A hand-written {@link Iterator} with its cursor state became {@code Stream.iterate} plus
 * a gatherer ({@code windowFixed}).
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class IteratorRow {

    static final int LAST = 7;
    static final int BATCH = 3;

    private IteratorRow() {}

    /** Before: the iterator keeps the cursor and builds each batch by hand. */
    public static final class Classic {

        static final class BatchIterator implements Iterator<List<Integer>> {
            private final int last;
            private final int size;
            private int next = 1;

            BatchIterator(int last, int size) {
                this.last = last;
                this.size = size;
            }

            @Override
            public boolean hasNext() {
                return next <= last;
            }

            @Override
            public List<Integer> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                List<Integer> batch = new ArrayList<>();
                while (batch.size() < size && next <= last) {
                    batch.add(next++);
                }
                return batch;
            }
        }

        private Classic() {}

        public static String run() {
            List<List<Integer>> batches = new ArrayList<>();
            new BatchIterator(LAST, BATCH).forEachRemaining(batches::add);
            return batches.toString();
        }
    }

    /** After: a generated stream, cut into windows by a JDK gatherer. */
    public static final class Modern {

        private Modern() {}

        public static String run() {
            return Stream.iterate(1, i -> i <= LAST, i -> i + 1).gather(Gatherers.windowFixed(BATCH)).toList()
                    .toString();
        }
    }
}
