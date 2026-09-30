package io.github.aliturgutbozkurt.patterns.m10.examples.immutable.config;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * Hot-reloadable configuration by copy-on-write: an {@link AtomicReference} always points to one complete,
 * immutable {@link ConfigSnapshot}. Readers take the current snapshot (old or new, never half of each) without a
 * lock; writers swap in a new one with {@code updateAndGet}, which retries on contention so no update is lost.
 *
 * @see "m10 lesson, section Immutable Object"
 */
public final class LiveConfig {

    private final AtomicReference<ConfigSnapshot> current;

    public LiveConfig(ConfigSnapshot initial) {
        this.current = new AtomicReference<>(Objects.requireNonNull(initial, "initial"));
    }

    /** The snapshot in force right now. Keep using the returned object: it will never change. */
    public ConfigSnapshot current() {
        return current.get();
    }

    /**
     * Applies {@code change} atomically and returns the new snapshot. {@code change} may run more than once under
     * contention, so it must be a pure function of its argument. If it throws, nothing is changed.
     */
    public ConfigSnapshot update(UnaryOperator<ConfigSnapshot> change) {
        return current.updateAndGet(change);
    }
}
