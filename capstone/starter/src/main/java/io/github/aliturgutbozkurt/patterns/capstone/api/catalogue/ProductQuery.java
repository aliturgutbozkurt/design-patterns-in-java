package io.github.aliturgutbozkurt.patterns.capstone.api.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * GIVEN — do not modify. Search criteria; a product matches when it matches all of them. Build queries from
 * {@link #all()} with the withers: {@code ProductQuery.all().inCategory(BOOKS).priceAtMost(Money.of("300"))}.
 *
 * @param categories    the allowed categories; empty means any category
 * @param maxPriceKurus the highest allowed price in kuruş (inclusive)
 * @param nameContains  text the name must contain, ignoring case; empty means any name
 * @see "capstone brief §2.2 — Catalogue (F1)"
 */
public record ProductQuery(Set<Category> categories, long maxPriceKurus, String nameContains) {

    public ProductQuery {
        categories = Set.copyOf(Objects.requireNonNull(categories, "categories"));
        Objects.requireNonNull(nameContains, "nameContains");
        if (maxPriceKurus < 0) {
            throw new IllegalArgumentException("negative maximum price: " + maxPriceKurus);
        }
    }

    /** Matches every product. */
    public static ProductQuery all() {
        return new ProductQuery(Set.of(), Long.MAX_VALUE, "");
    }

    /** This query, also allowing {@code category} (calling it twice allows both categories). */
    public ProductQuery inCategory(Category category) {
        Set<Category> more = EnumSet.of(Objects.requireNonNull(category, "category"));
        more.addAll(categories);
        return new ProductQuery(more, maxPriceKurus, nameContains);
    }

    /** This query, restricted to prices ≤ {@code max}. */
    public ProductQuery priceAtMost(Money max) {
        return new ProductQuery(categories, max.kurus(), nameContains);
    }

    /** This query, restricted to names containing {@code text} (ignoring case). */
    public ProductQuery nameContaining(String text) {
        return new ProductQuery(categories, maxPriceKurus, text);
    }
}
