package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * The catalogue's search criteria; combine them with {@link Specification#and}.
 *
 * @see "capstone guide §1 Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.SPECIFICATION, role = "concrete specifications")
public final class ProductSpecs {

    private ProductSpecs() {
    }

    /** In one of {@code categories}; an empty set matches every product. */
    public static Specification<Product> inAnyCategory(Set<Category> categories) {
        Set<Category> allowed = Set.copyOf(categories);
        return product -> allowed.isEmpty() || allowed.contains(product.category());
    }

    /** Price ≤ {@code limit}. */
    public static Specification<Product> priceAtMost(Money limit) {
        Objects.requireNonNull(limit, "limit");
        return product -> product.price().compareTo(limit) <= 0;
    }

    /** Name contains {@code text}, ignoring case; an empty text matches every product. */
    public static Specification<Product> nameContains(String text) {
        String needle = Objects.requireNonNull(text, "text").toLowerCase(Locale.ROOT);
        return product -> product.name().toLowerCase(Locale.ROOT).contains(needle);
    }
}
