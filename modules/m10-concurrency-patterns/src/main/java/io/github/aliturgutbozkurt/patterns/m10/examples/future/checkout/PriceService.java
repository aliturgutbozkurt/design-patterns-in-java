package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

import java.util.concurrent.CompletableFuture;

/**
 * An asynchronous price look-up (like an HTTP client's {@code sendAsync}).
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
@FunctionalInterface
public interface PriceService {

    CompletableFuture<Long> totalCents(Cart cart);
}
