package io.github.aliturgutbozkurt.patterns.capstone.api.sim;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec;
import java.time.LocalDate;

/**
 * GIVEN — do not modify. The sample catalogue and promotions of brief §2.2, added through the public use cases. The
 * acceptance tests and {@code Main --demo} start from it.
 *
 * @see "capstone brief §2.2 — Pricing (F4), worked example"
 */
public final class DemoData {

    private DemoData() {
    }

    /** Adds the six sample products and the four sample promotions to {@code shop}. */
    public static void seed(PatternShop shop) {
        product(shop, "BOK-001", "Design Patterns Handbook", Category.BOOKS, ProductType.PHYSICAL, "250.00", 20);
        product(shop, "BOK-002", "Java 27 in Action", Category.BOOKS, ProductType.PHYSICAL, "400.00", 8);
        product(shop, "TOY-001", "Pattern Puzzle", Category.TOYS, ProductType.PHYSICAL, "120.00", 6);
        product(shop, "HOM-001", "Hexagon Mug", Category.HOME, ProductType.PHYSICAL, "89.90", 50);
        product(shop, "ELE-001", "USB-C Hub", Category.ELECTRONICS, ProductType.PHYSICAL, "649.00", 10);
        product(shop, "DIG-001", "E-book Bundle", Category.BOOKS, ProductType.DIGITAL, "99.90", 0);

        shop.pricing().addPromotion(new PromotionSpec.BuyXGetYFree(new Sku("TOY-001"), 2, 1));
        shop.pricing().addPromotion(new PromotionSpec.CategoryPercentOff(Category.BOOKS, 10));
        shop.pricing().addPromotion(new PromotionSpec.AmountOffOver(Money.of("1000.00"), Money.of("100.00")));
        shop.pricing().addPromotion(new PromotionSpec.Coupon("AUTUMN5", 5, LocalDate.of(2026, 12, 31)));
    }

    private static void product(PatternShop shop, String sku, String name, Category category, ProductType type,
                                String price, int stock) {
        shop.catalogue().add(new ProductSpec(new Sku(sku), name, category, type, Money.of(price), stock));
    }
}
