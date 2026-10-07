package io.github.aliturgutbozkurt.patterns.capstone.api.event;

import java.util.function.Consumer;

/**
 * GIVEN — do not modify. Inbound port of feature F8: subscribe to the shop's domain events.
 *
 * <p>Events are dispatched only after the change that raised them is stored, in the order they were raised, so a
 * handler sees the new state. A handler receives the events of its type and its subtypes (a {@link ShopEvent} handler
 * receives all). If a handler throws, the exception goes to {@code ShopEnvironment.errors()}, the other handlers still
 * run and the operation still succeeds.
 *
 * @see "capstone brief, Business rules — Events and notifications (F8)"
 */
public interface ShopEvents {

    /** Subscribes {@code handler} to events of {@code type}; close the subscription to stop receiving them. */
    <E extends ShopEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler);
}
