package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.Changes;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.PhysicalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.StockLevels;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.OrderItem;
import java.util.List;
import java.util.Objects;

/**
 * Stock bookkeeping shared by checkout (reserve) and cancellation (release); raises {@code StockLow} on a crossing.
 * Call it inside a transaction.
 *
 * @see "capstone guide §2 Slice walkthrough — C5"
 */
public final class Inventory {

    private final ProductRepository products;
    private final int lowStockThreshold;

    public Inventory(ProductRepository products, int lowStockThreshold) {
        this.products = Objects.requireNonNull(products, "products");
        this.lowStockThreshold = lowStockThreshold;
    }

    /** Units in stock of {@code sku} (0 for digital or unknown products). */
    public int stockOf(Sku sku) {
        return products.find(sku).map(Product::stock).orElse(0);
    }

    /** Takes the physical lines out of stock (validated before), raising an alert for every crossing. */
    public void reserve(List<OrderItem> items, Changes changes) {
        for (OrderItem item : items) {
            if (products.find(item.sku()).orElseThrow() instanceof PhysicalProduct before) {
                PhysicalProduct after = before.reserved(item.quantity());
                products.save(after);
                StockLevels.alert(before, after, lowStockThreshold).ifPresent(changes::raise);
            }
        }
    }

    /** Puts the physical lines back into stock. */
    public void release(List<OrderItem> items) {
        for (OrderItem item : items) {
            if (products.find(item.sku()).orElseThrow() instanceof PhysicalProduct product) {
                products.save(product.restocked(item.quantity()));
            }
        }
    }
}
