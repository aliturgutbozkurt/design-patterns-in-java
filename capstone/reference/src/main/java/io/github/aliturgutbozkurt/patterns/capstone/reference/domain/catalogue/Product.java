package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;

/**
 * A catalogue product: physical (with stock) or digital (unlimited). Immutable — a stock change is a new value, so a
 * product can be shared between threads without locks.
 *
 * @see "capstone guide §1 Pattern map — Immutable Object"
 */
@PatternRole(value = DesignPattern.IMMUTABLE_OBJECT, role = "immutable value (sealed hierarchy of records)")
public sealed interface Product permits PhysicalProduct, DigitalProduct {

    Sku sku();

    String name();

    Category category();

    Money price();

    /** Physical or digital. */
    ProductType type();

    /** Units in stock; 0 for a digital product. */
    int stock();
}
