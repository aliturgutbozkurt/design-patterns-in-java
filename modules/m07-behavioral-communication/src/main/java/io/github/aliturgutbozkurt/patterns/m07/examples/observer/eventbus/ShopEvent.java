package io.github.aliturgutbozkurt.patterns.m07.examples.observer.eventbus;

/**
 * Closed set of PatternShop domain events carried by the {@link EventBus}; handlers can switch over it exhaustively.
 *
 * @see "m07 lesson, section Observer — typed event bus"
 */
public sealed interface ShopEvent permits OrderPlaced, PaymentFailed, OrderShipped {

    /** The order the event is about. */
    String orderId();
}
