package io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog;

import java.util.Locale;
import java.util.Objects;

/**
 * The catalogue's building blocks for queries; combine them with {@code and}, {@code or} and {@code not}.
 *
 * @see "m11 lesson, section Repository — Specification"
 */
public final class ProductSpecs {

    private ProductSpecs() {}

    public static Specification<Product> inCategory(Category category) {
        Objects.requireNonNull(category, "category");
        return product -> product.category() == category;
    }

    public static Specification<Product> priceAtMost(Money limit) {
        Objects.requireNonNull(limit, "limit");
        return product -> product.price().compareTo(limit) <= 0;
    }

    /** Case-insensitive substring match on the name. */
    public static Specification<Product> nameContains(String text) {
        String needle = Objects.requireNonNull(text, "text").toLowerCase(Locale.ROOT);
        return product -> product.name().toLowerCase(Locale.ROOT).contains(needle);
    }
}
