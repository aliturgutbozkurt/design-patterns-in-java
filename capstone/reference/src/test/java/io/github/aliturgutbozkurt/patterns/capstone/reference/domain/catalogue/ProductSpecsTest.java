package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductSpecsTest {

    private final Product puzzle = new PhysicalProduct(new Sku("TOY-001"), "Pattern Puzzle", Category.TOYS,
            Money.of("120.00"), 6);

    @Test
    void emptyCriteriaMatchEverything() {
        assertThat(ProductSpecs.inAnyCategory(Set.of()).isSatisfiedBy(puzzle)).isTrue();
        assertThat(ProductSpecs.nameContains("").isSatisfiedBy(puzzle)).isTrue();
    }

    @Test
    void combinedCriteriaMustAllMatch() {
        Specification<Product> toysUpTo120NamedPattern = ProductSpecs.inAnyCategory(Set.of(Category.TOYS))
                .and(ProductSpecs.priceAtMost(Money.of("120.00")))
                .and(ProductSpecs.nameContains("PATTERN"));

        assertThat(toysUpTo120NamedPattern.isSatisfiedBy(puzzle)).isTrue();
        assertThat(ProductSpecs.priceAtMost(Money.of("119.99")).isSatisfiedBy(puzzle)).isFalse();
        assertThat(ProductSpecs.inAnyCategory(Set.of(Category.BOOKS, Category.HOME)).isSatisfiedBy(puzzle)).isFalse();
    }
}
