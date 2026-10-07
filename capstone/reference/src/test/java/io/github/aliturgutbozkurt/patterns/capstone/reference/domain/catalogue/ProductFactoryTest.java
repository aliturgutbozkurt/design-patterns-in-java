package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import org.junit.jupiter.api.Test;

class ProductFactoryTest {

    private static final Sku SKU = new Sku("BOK-001");
    private static final Money PRICE = Money.of("250.00");

    @Test
    void eachProductTypeHasItsOwnFactory() {
        assertThat(ProductFactory.forType(ProductType.PHYSICAL).create(SKU, "Book", Category.BOOKS, PRICE, 3))
                .isEqualTo(new PhysicalProduct(SKU, "Book", Category.BOOKS, PRICE, 3));
        assertThat(ProductFactory.forType(ProductType.DIGITAL).create(SKU, "E-book", Category.BOOKS, PRICE, 0))
                .isEqualTo(new DigitalProduct(SKU, "E-book", Category.BOOKS, PRICE));
    }

    @Test
    void digitalFactoryRejectsStock() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductFactory.DIGITAL.create(SKU, "E-book", Category.BOOKS, PRICE, 1))
                .withMessage("a digital product has no stock: 1");
    }

    @Test
    void productsRejectBlankNamesZeroPricesAndNegativeStock() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductFactory.PHYSICAL.create(SKU, " ", Category.BOOKS, PRICE, 1));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductFactory.PHYSICAL.create(SKU, "Book", Category.BOOKS, Money.ZERO, 1));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ProductFactory.PHYSICAL.create(SKU, "Book", Category.BOOKS, PRICE, -1));
    }

    @Test
    void physicalStockChangesReturnNewValues() {
        PhysicalProduct book = new PhysicalProduct(SKU, "Book", Category.BOOKS, PRICE, 5);

        assertThat(book.restocked(2).stock()).isEqualTo(7);
        assertThat(book.reserved(5).stock()).isZero();
        assertThat(book.stock()).as("unchanged").isEqualTo(5);
        assertThatIllegalArgumentException().isThrownBy(() -> book.restocked(0));
        assertThatIllegalStateException().isThrownBy(() -> book.reserved(6));
    }
}
