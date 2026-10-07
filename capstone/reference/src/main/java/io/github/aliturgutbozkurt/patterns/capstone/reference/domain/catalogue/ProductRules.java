package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.Objects;

/** The rules every product shares (brief §2.2 "Catalogue"). */
final class ProductRules {

    private ProductRules() {
    }

    static void requireNameAndPrice(String name, Money price) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(price, "price");
        if (name.isBlank()) {
            throw new IllegalArgumentException("blank product name");
        }
        if (price.isZero()) {
            throw new IllegalArgumentException("price must be positive");
        }
    }
}
