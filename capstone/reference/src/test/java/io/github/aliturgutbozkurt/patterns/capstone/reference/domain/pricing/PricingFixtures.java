package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.time.LocalDate;
import java.util.List;

/** Basket lines and promotions of the brief's sample data, for the pricing unit tests. */
final class PricingFixtures {

    static final LocalDate TODAY = LocalDate.of(2026, 11, 16);
    static final PromotionRule TOYS_3_FOR_2 = new BuyXGetYFreeRule(new Sku("TOY-001"), 2, 1);
    static final PromotionRule BOOKS_10 = new CategoryPercentOffRule(Category.BOOKS, 10);
    static final PromotionRule HUNDRED_OFF = new AmountOffOverRule(Money.of("1000.00"), Money.of("100.00"));
    static final PromotionRule AUTUMN5 = new CouponRule("AUTUMN5", 5, LocalDate.of(2026, 12, 31));
    static final List<PromotionRule> DEMO = List.of(TOYS_3_FOR_2, BOOKS_10, HUNDRED_OFF, AUTUMN5);

    private PricingFixtures() {
    }

    static BasketLine line(String sku, Category category, ProductType type, int quantity, String price) {
        return new BasketLine(new Sku(sku), sku, category, type, quantity, Money.of(price));
    }

    /** The cart of the brief's worked example. */
    static List<BasketLine> workedExample() {
        return List.of(
                line("BOK-001", Category.BOOKS, ProductType.PHYSICAL, 2, "250.00"),
                line("BOK-002", Category.BOOKS, ProductType.PHYSICAL, 1, "400.00"),
                line("TOY-001", Category.TOYS, ProductType.PHYSICAL, 3, "120.00"),
                line("DIG-001", Category.BOOKS, ProductType.DIGITAL, 1, "99.90"));
    }

    static PriceSheet sheet(BasketLine... lines) {
        return new BasePrices().price(new Basket(List.of(lines), "", TODAY));
    }
}
