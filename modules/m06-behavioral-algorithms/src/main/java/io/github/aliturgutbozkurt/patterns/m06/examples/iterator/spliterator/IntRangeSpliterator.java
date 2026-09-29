package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.spliterator;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The integers in {@code [from, to)}. It knows its exact size and splits in halves, so a parallel stream can hand
 * each half to a different thread.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class IntRangeSpliterator implements Spliterator<Integer> {

    private int from;
    private final int to;

    public IntRangeSpliterator(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("from > to: " + from + " > " + to);
        }
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Integer> action) {
        if (from >= to) {
            return false;
        }
        action.accept(from++);
        return true;
    }

    /** Gives away the first half and keeps the second; {@code null} when too small to split. */
    @Override
    public Spliterator<Integer> trySplit() {
        int size = to - from;
        if (size < 2) {
            return null;
        }
        int middle = from + size / 2;
        var firstHalf = new IntRangeSpliterator(from, middle);
        from = middle;
        return firstHalf;
    }

    @Override
    public long estimateSize() {
        return (long) to - from;
    }

    @Override
    public int characteristics() {
        return ORDERED | SIZED | SUBSIZED;
    }
}
