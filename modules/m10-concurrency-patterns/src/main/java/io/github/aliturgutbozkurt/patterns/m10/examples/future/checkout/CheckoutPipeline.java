package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * A {@link CompletableFuture} pipeline. Price and stock are independent, so they run at the same time and
 * {@code thenCombine} waits for both; payment depends on the price, so {@code thenCompose} chains it without a
 * nested {@code CompletableFuture<CompletableFuture<…>>}; {@code exceptionally} turns any failure into a
 * declined receipt. Nothing blocks: {@code checkout} returns a future at once.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public final class CheckoutPipeline {

    private final Executor executor;
    private final PriceService prices;
    private final StockService stock;
    private final PaymentService payments;

    /** Continuations run on {@code executor}; pass {@code Runnable::run} for a synchronous, deterministic run. */
    public CheckoutPipeline(Executor executor, PriceService prices, StockService stock, PaymentService payments) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.prices = Objects.requireNonNull(prices, "prices");
        this.stock = Objects.requireNonNull(stock, "stock");
        this.payments = Objects.requireNonNull(payments, "payments");
    }

    public CompletableFuture<Receipt> checkout(Cart cart) {
        CompletableFuture<Long> total = prices.totalCents(cart);          // both start right away
        CompletableFuture<Void> reserved = stock.reserve(cart);
        return total
                .thenCombine(reserved, (cents, _) -> cents)                 // wait for both independent results
                .thenComposeAsync(cents -> payments.charge(cart.customer(), cents)   // dependent async step
                        .thenApply(paymentId -> Receipt.approved(cart.customer(), cents, paymentId)), executor)
                .exceptionally(failure -> Receipt.declined(cart.customer(), unwrap(failure).getMessage()));
    }

    /** A dependent stage sees the original exception wrapped in a {@link CompletionException}. */
    private static Throwable unwrap(Throwable failure) {
        return failure instanceof CompletionException && failure.getCause() != null ? failure.getCause() : failure;
    }
}
