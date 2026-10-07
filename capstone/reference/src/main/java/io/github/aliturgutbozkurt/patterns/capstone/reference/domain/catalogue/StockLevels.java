package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent.StockLow;
import java.util.Optional;

/**
 * The low-stock rule of the brief's Business rules: an alert when the stock drops from at least the threshold to below
 * it — once per crossing, because staying low or rising does not cross.
 *
 * @see "capstone guide, Slice walkthrough — C5"
 */
public final class StockLevels {

    private StockLevels() {
    }

    /** The alert for a change from {@code before} to {@code after}, if it crosses below {@code threshold}. */
    public static Optional<StockLow> alert(PhysicalProduct before, PhysicalProduct after, int threshold) {
        if (before.stock() >= threshold && after.stock() < threshold) {
            return Optional.of(new StockLow(after.sku(), after.stock()));
        }
        return Optional.empty();
    }
}
