package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Makes stream laziness visible: {@code peek} records every step, so the order of the calls shows that elements
 * travel through the pipeline one at a time and that a short-circuiting terminal operation stops early.
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class LazyTrace {

    private final List<String> trace = new ArrayList<>();

    /** Everything recorded so far, in call order. */
    public List<String> trace() {
        return List.copyOf(trace);
    }

    public Optional<Integer> firstEven(List<Integer> numbers) {
        return numbers.stream()
                .peek(n -> trace.add("see " + n))
                .filter(n -> n % 2 == 0)
                .peek(n -> trace.add("even " + n))
                .findFirst();
    }

    /** Builds the same pipeline but closes it without a terminal operation: nothing is recorded. */
    public void withoutTerminalOperation(List<Integer> numbers) {
        try (Stream<Integer> _ = numbers.stream().peek(n -> trace.add("see " + n)).filter(n -> n % 2 == 0)) {
            // the pipeline exists, but nothing ever pulls an element through it
        }
    }

    /** {@code Stream.iterate} is infinite; {@code limit} makes it finite. */
    public static List<Integer> powersOfTwo(int count) {
        return Stream.iterate(1, x -> 2 * x).limit(count).toList();
    }
}
