package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.DigitalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.PhysicalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CategoryPercentOffRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InMemoryRepositoriesTest {

    @Test
    void productsComeBackSortedBySkuAndSaveReplaces() {
        var repository = new InMemoryProductRepository();
        Product toy = new PhysicalProduct(new Sku("TOY-001"), "Puzzle", Category.TOYS, Money.of("1.00"), 1);
        Product book = new DigitalProduct(new Sku("BOK-001"), "E-book", Category.BOOKS, Money.of("2.00"));
        repository.save(toy);
        repository.save(book);
        repository.save(((PhysicalProduct) toy).restocked(4));

        assertThat(repository.findMatching(_ -> true)).extracting(Product::sku)
                .containsExactly(new Sku("BOK-001"), new Sku("TOY-001"));
        assertThat(repository.find(new Sku("TOY-001"))).get().extracting(Product::stock).isEqualTo(5);
    }

    @Test
    void promotionsKeepRegistrationOrderAndFindCouponsByCode() {
        var repository = new InMemoryPromotionRepository();
        var books = new CategoryPercentOffRule(Category.BOOKS, 10);
        var coupon = new CouponRule("AUTUMN5", 5, LocalDate.of(2026, 12, 31));
        repository.add(books);
        repository.add(coupon);

        assertThat(repository.all()).containsExactly(books, coupon);
        assertThat(repository.coupon("AUTUMN5")).contains(coupon);
        assertThat(repository.coupon("autumn5")).isEmpty();
    }
}
