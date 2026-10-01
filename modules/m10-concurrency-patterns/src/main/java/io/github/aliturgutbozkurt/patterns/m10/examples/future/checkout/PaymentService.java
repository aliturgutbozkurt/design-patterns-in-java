package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

import java.util.concurrent.CompletableFuture;

/**
 * Charges the customer asynchronously and completes with a payment id.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
@FunctionalInterface
public interface PaymentService {

    CompletableFuture<String> charge(String customer, long cents);
}
