package io.github.aliturgutbozkurt.patterns.capstone.api.event;

/**
 * GIVEN — do not modify. A handle on one subscription.
 *
 * @see "capstone brief §2.2 — Events and notifications (F8)"
 */
@FunctionalInterface
public interface Subscription {

    /** Stops the deliveries to this subscription's handler; calling it again does nothing. */
    void close();
}
