package io.github.aliturgutbozkurt.patterns.m09.examples.result.jdk;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Price lookups written three times with the same {@code map}/{@code flatMap} shape: over {@link Optional}, over
 * {@link Stream} and over {@link CompletableFuture} ({@code thenApply}/{@code thenCompose}, with {@code handle} as
 * the fold). The futures here are already completed; asynchronous pipelines are m10's topic.
 *
 * @see "m09 lesson, section Optional and Result — the same shape in the JDK"
 */
public final class JdkResultShapes {

    private static final long FREE_SHIPPING_FROM_CENTS = 50_00;
    private static final long SHIPPING_CENTS = 4_99;

    private final Map<String, Long> prices;
    private final Map<String, String> bundles;

    public JdkResultShapes(Map<String, Long> prices, Map<String, String> bundles) {
        this.prices = Map.copyOf(prices);
        this.bundles = Map.copyOf(bundles);
    }

    /** Mugs are sold with a coaster; T-shirts come alone. */
    public static JdkResultShapes sample() {
        return new JdkResultShapes(Map.of("MUG-0001", 12_50L, "TEE-0002", 20_00L, "COASTER-0003", 10_00L),
                Map.of("MUG-0001", "COASTER-0003"));
    }

    public Optional<Long> price(String sku) {
        return Optional.ofNullable(prices.get(sku));
    }

    /** The SKU sold together with {@code sku}, if any. */
    public Optional<String> bundleOf(String sku) {
        return Optional.ofNullable(bundles.get(sku));
    }

    public static long withTax(long cents) {
        return cents * 120 / 100;
    }

    /** {@code Stream.flatMap}: each SKU expands to itself plus its bundled SKU. */
    public List<String> bundleSkus(List<String> skus) {
        return skus.stream().flatMap(sku -> Stream.concat(Stream.of(sku), bundleOf(sku).stream())).toList();
    }

    /** {@code flatMap(Optional::stream)}: unknown SKUs are dropped, the order is kept. */
    public List<Long> knownPrices(List<String> skus) {
        return skus.stream().map(this::price).flatMap(Optional::stream).toList();
    }

    public CompletableFuture<Long> priceAsync(String sku) {
        return price(sku)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.failedFuture(new NoSuchElementException("unknown sku " + sku)));
    }

    /** {@code thenApply} is {@code map}, {@code thenCompose} is {@code flatMap}. */
    public CompletableFuture<Long> totalAsync(String sku) {
        return priceAsync(sku)
                .thenApply(JdkResultShapes::withTax)
                .thenCompose(this::withShippingAsync);
    }

    /** {@code handle} is the fold: it sees either the value or the failure and always produces a value. */
    public CompletableFuture<String> describeAsync(String sku) {
        return totalAsync(sku).handle((total, failure) -> failure == null
                ? sku + ": " + money(total) + " incl. tax and shipping"
                : sku + ": failed (" + FutureResults.cause(failure).getMessage() + ")");
    }

    private CompletableFuture<Long> withShippingAsync(long cents) {
        return CompletableFuture.completedFuture(cents >= FREE_SHIPPING_FROM_CENTS ? cents : cents + SHIPPING_CENTS);
    }

    private static String money(long cents) {
        return String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100);
    }
}
