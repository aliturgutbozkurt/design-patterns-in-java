package io.github.aliturgutbozkurt.patterns.capstone.api.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * GIVEN — do not modify. Inbound port of feature F1: the product catalogue.
 *
 * @see "capstone brief, Business rules — Catalogue (F1)"
 */
public interface CatalogueUseCase {

    /**
     * Adds a product. Rejected with {@link IllegalArgumentException} (and nothing is added) when the name is blank, the
     * price is not positive, a physical product's stock is negative, a digital product's stock is not 0, or the SKU
     * already exists (message {@code duplicate SKU: BOK-001}).
     */
    ProductView add(ProductSpec spec);

    /** The product with this SKU, if any. */
    Optional<ProductView> find(Sku sku);

    /** The products matching every criterion of the query, sorted by SKU. */
    List<ProductView> search(ProductQuery query);

    /**
     * Adds {@code quantity} units to a physical product's stock and returns the updated product. Rejected with
     * {@link IllegalArgumentException} for a digital product or a quantity ≤ 0; an unknown SKU throws
     * {@link NoSuchElementException} ({@code unknown product: XXX-999}).
     */
    ProductView restock(Sku sku, int quantity);
}
