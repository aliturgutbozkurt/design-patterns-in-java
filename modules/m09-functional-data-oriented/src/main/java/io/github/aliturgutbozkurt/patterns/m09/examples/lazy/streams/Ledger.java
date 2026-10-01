package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.streams;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * An account ledger with Stream Gatherers: {@code scan} emits every running balance, {@code fold} emits only the
 * last one (and still emits the initial value for an empty stream).
 *
 * @see "m09 lesson, section Lazy evaluation and memoisation"
 */
public final class Ledger {

    private Ledger() {}

    public static List<Long> runningBalances(List<Long> movements) {
        return movements.stream().gather(Gatherers.scan(() -> 0L, Long::sum)).toList();
    }

    public static List<Long> closingBalance(List<Long> movements) {
        return movements.stream().gather(Gatherers.fold(() -> 0L, Long::sum)).toList();
    }
}
