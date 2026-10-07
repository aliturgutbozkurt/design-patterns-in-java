package io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment;

/**
 * GIVEN — do not modify. Inbound port of feature F9: ship every order that is {@code PAID} at the moment of the call.
 *
 * @see "capstone brief §2.2 — Fulfilment (F9)"
 */
public interface FulfilmentUseCase {

    /**
     * Ships the paid orders in parallel on virtual threads (never more than {@code ShopSettings.maxParallelOrders()} at
     * once) and returns when all of them are finished. Results and {@code OrderShipped} events are in order-number
     * order; the events are dispatched on the calling thread after the run.
     */
    FulfilmentReport fulfilPaidOrders();
}
