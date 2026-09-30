package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.gate;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A price service that must warm its cache before it can answer. Requests that arrive during start-up are not
 * rejected: they wait at the {@link ReadinessGate} until the cache is warm (or the warm-up failed).
 *
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public final class WarmingService {

    private final Supplier<Map<String, String>> loader;
    private final ReadinessGate gate = new ReadinessGate();
    private Map<String, String> cache = Map.of();   // written before markReady(), read after awaitReady(): the
                                                    // gate's lock makes the write visible to every reader

    public WarmingService(Supplier<Map<String, String>> loader) {
        this.loader = Objects.requireNonNull(loader, "loader");
    }

    /** Loads the cache and opens the gate; a failing loader fails the gate and is rethrown. */
    public void warmUp() {
        try {
            cache = Map.copyOf(loader.get());
        } catch (RuntimeException e) {
            gate.markFailed(e);
            throw e;
        }
        gate.markReady();
    }

    /** Answers from the cache, waiting up to {@code maxWait} for the warm-up. */
    public Optional<String> lookup(String key, Duration maxWait) throws InterruptedException {
        if (!gate.awaitReady(maxWait)) {
            throw new IllegalStateException("service not ready after " + maxWait);
        }
        return Optional.ofNullable(cache.get(key));
    }

    public ReadinessGate gate() {
        return gate;
    }
}
