package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * All three services, simulated in memory. Every call runs via {@code supplyAsync} on the injected
 * {@link Executor}; with {@code Runnable::run} each future is already complete when the method returns.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public final class InMemoryShop implements PriceService, StockService, PaymentService {

    private final Executor executor;
    private final Map<String, Long> prices;
    private final Set<String> inStock;
    private final long cardLimitCents;
    private final AtomicInteger payments = new AtomicInteger();

    public InMemoryShop(Executor executor, Map<String, Long> prices, Set<String> inStock, long cardLimitCents) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.prices = Map.copyOf(prices);
        this.inStock = Set.copyOf(inStock);
        this.cardLimitCents = cardLimitCents;
    }

    @Override
    public CompletableFuture<Long> totalCents(Cart cart) {
        return CompletableFuture.supplyAsync(() -> cart.skus().stream().mapToLong(sku -> {
            Long price = prices.get(sku);
            if (price == null) {
                throw new IllegalArgumentException("unknown sku: " + sku);
            }
            return price;
        }).sum(), executor);
    }

    @Override
    public CompletableFuture<Void> reserve(Cart cart) {
        return CompletableFuture.runAsync(() -> cart.skus().stream()
                .filter(sku -> !inStock.contains(sku))
                .findFirst()
                .ifPresent(sku -> {
                    throw new IllegalStateException("out of stock: " + sku);
                }), executor);
    }

    @Override
    public CompletableFuture<String> charge(String customer, long cents) {
        return CompletableFuture.supplyAsync(() -> {
            int id = payments.incrementAndGet();
            if (cents > cardLimitCents) {
                throw new IllegalStateException(String.format(Locale.ROOT, "card declined: %d.%02d exceeds limit",
                        cents / 100, cents % 100));
            }
            return "pay-" + id;
        }, executor);
    }

    /** How often {@link #charge} was called. */
    public int paymentCalls() {
        return payments.get();
    }
}
