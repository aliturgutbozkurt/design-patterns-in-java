package io.github.aliturgutbozkurt.patterns.capstone.api;

import java.util.Objects;

/**
 * GIVEN — do not modify. Configuration of one shop.
 *
 * @param merchantId        sent to the payment provider
 * @param maxParallelOrders how many orders fulfilment may process at once, at least 1
 * @param lowStockThreshold stock below this is low ({@code StockLow}, inventory report), at least 0
 * @see "capstone brief §2.2"
 */
public record ShopSettings(String merchantId, int maxParallelOrders, int lowStockThreshold) {

    public ShopSettings {
        Objects.requireNonNull(merchantId, "merchantId");
        if (merchantId.isBlank()) {
            throw new IllegalArgumentException("blank merchant id");
        }
        if (maxParallelOrders < 1) {
            throw new IllegalArgumentException("maxParallelOrders must be positive: " + maxParallelOrders);
        }
        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException("negative lowStockThreshold: " + lowStockThreshold);
        }
    }

    /** {@code ("PATTERNSHOP", 4, 5)} */
    public static ShopSettings defaults() {
        return new ShopSettings("PATTERNSHOP", 4, 5);
    }
}
