package io.github.aliturgutbozkurt.patterns.m10.examples.structured.context;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * Scoped values and structured concurrency fit together: a value bound around a scope is <em>inherited</em> by
 * every subtask forked in it, for exactly as long as the scope lives. Plain executor threads do not get it (see
 * {@code scopedvalue.threadlocal.InheritanceFacts}). Preview API (JEP 533).
 *
 * @see "m10 lesson, section Structured Concurrency"
 */
public final class ScopedFanOut {

    /** The trace id of the current request. */
    public static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();

    private ScopedFanOut() {}

    /** Calls every downstream service in its own subtask; each result shows the trace id the subtask saw. */
    public static List<String> traceAll(String traceId, List<String> services) throws InterruptedException {
        return ScopedValue.where(TRACE_ID, traceId).call(() -> {
            try (var scope = StructuredTaskScope.open(
                    Joiner.<String, IllegalStateException>allSuccessfulOrThrow(IllegalStateException::new))) {
                for (String service : services) {
                    scope.fork(() -> TRACE_ID.get() + " -> " + service);   // runs on another (virtual) thread
                }
                return scope.join();                                        // results in fork order
            }
        });
    }
}
