package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound;

/**
 * Inbound port of the PatternShop core: the one thing the CLI (or a future HTTP adapter) may call.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
@FunctionalInterface
public interface PlaceOrderUseCase {

    /** Places an order; business refusals come back as {@link PlaceOrderResult.Rejected}. */
    PlaceOrderResult place(PlaceOrderCommand command);
}
