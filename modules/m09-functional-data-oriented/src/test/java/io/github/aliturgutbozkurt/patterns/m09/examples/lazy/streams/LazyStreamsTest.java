package io.github.aliturgutbozkurt.patterns.m09.examples.lazy.streams;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LazyStreamsTest {

    @Test
    void streamsProcessOneElementAtATimeAndStopAtTheFirstMatch() {
        var tracer = new LazyTrace();
        assertThat(tracer.firstEven(List.of(1, 2, 3, 4, 5))).contains(2);
        assertThat(tracer.trace()).containsExactly("see 1", "see 2", "even 2");
    }

    @Test
    void withoutATerminalOperationNothingRuns() {
        var tracer = new LazyTrace();
        tracer.withoutTerminalOperation(List.of(1, 2, 3));
        assertThat(tracer.trace()).isEmpty();
    }

    @Test
    void anInfiniteIterateIsCutByLimit() {
        assertThat(LazyTrace.powersOfTwo(5)).containsExactly(1, 2, 4, 8, 16);
    }

    @Test
    void aDisabledLevelNeverInvokesTheMessageSupplier() {
        var lines = new ArrayList<String>();
        var calls = new AtomicInteger();
        var log = new LazyLog(Level.INFO, lines::add);
        log.log(Level.DEBUG, () -> "expensive " + calls.incrementAndGet());
        log.log(Level.WARNING, () -> "stock low " + calls.incrementAndGet());
        assertThat(calls).hasValue(1);
        assertThat(lines).containsExactly("WARNING stock low 1");
        assertThat(log.isEnabled(Level.DEBUG)).isFalse();
    }

    @Test
    void scanGivesRunningBalancesAndFoldGivesTheClosingBalance() {
        assertThat(Ledger.runningBalances(List.of(100L, -30L, 5L))).containsExactly(100L, 70L, 75L);
        assertThat(Ledger.closingBalance(List.of(100L, -30L, 5L))).containsExactly(75L);
    }

    @Test
    void onAnEmptyStreamScanIsEmptyButFoldGivesTheInitialValue() {
        assertThat(Ledger.runningBalances(List.of())).isEmpty();
        assertThat(Ledger.closingBalance(List.of())).containsExactly(0L);
    }

    @Test
    void demoPrintsTheTraceShortCircuitingLazyLogAndLedger() {
        assertThat(Console.capture(() -> LazyStreamsDemo.main(new String[0]))).isEqualTo("""
                -- vertical, one element at a time; findFirst stops early
                [see 1, see 2, even 2] -> Optional[2]
                -- no terminal operation, no work
                trace without a terminal operation: []
                -- an infinite stream, cut by limit
                Stream.iterate(1, x -> 2 * x).limit(5) = [1, 2, 4, 8, 16]
                -- a message supplier runs only if the level is enabled
                WARNING cart CART-7 has 3 items
                message suppliers called: 1
                -- running balance (scan) vs. closing balance (fold)
                movements [100, -30, 5]
                scan -> [100, 70, 75]
                fold -> [75]
                """);
    }
}
