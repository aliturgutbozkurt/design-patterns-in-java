package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

import java.util.concurrent.CompletableFuture;

/**
 * Reserves the cart's items asynchronously; the future fails if an item is out of stock.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
@FunctionalInterface
public interface StockService {

    CompletableFuture<Void> reserve(Cart cart);
}
