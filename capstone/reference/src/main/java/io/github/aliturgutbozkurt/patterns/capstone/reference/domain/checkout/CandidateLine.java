package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.checkout;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Objects;

/**
 * A cart line as checkout validation sees it: what is wanted and what is in stock.
 *
 * @param sku      the product
 * @param type     physical or digital
 * @param quantity units wanted
 * @param stock    units in stock (ignored for digital products)
 * @see "capstone guide, Pattern map — Chain of Responsibility"
 */
public record CandidateLine(Sku sku, ProductType type, int quantity, int stock) {

    public CandidateLine {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(type, "type");
    }

    /** Whether the line must be shipped. */
    public boolean isPhysical() {
        return type == ProductType.PHYSICAL;
    }
}
