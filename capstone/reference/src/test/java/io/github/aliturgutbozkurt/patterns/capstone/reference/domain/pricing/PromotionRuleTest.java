package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import static io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingFixtures.line;
import static io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingFixtures.sheet;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PromotionRuleTest {

    @Test
    void buyXGetYCountsWholeGroupsOnly() {
        PromotionRule rule = new BuyXGetYFreeRule(new Sku("TOY-001"), 2, 1);

        PriceSheet eight = rule.applyTo(sheet(line("TOY-001", Category.TOYS, ProductType.PHYSICAL, 8, "10.00")));

        assertThat(eight.discounts()).containsExactly(new Discount("buy 2 get 1 free: TOY-001", Money.of("20.00")));
        assertThat(eight.lines().getFirst().afterFreeUnits()).isEqualTo(Money.of("60.00"));
    }

    @Test
    void categoryPercentIsTakenFromWhatIsLeftAfterFreeUnits() {
        PriceSheet afterFreeUnits = new BuyXGetYFreeRule(new Sku("BOK-001"), 1, 1)
                .applyTo(sheet(line("BOK-001", Category.BOOKS, ProductType.PHYSICAL, 2, "250.00")));

        PriceSheet result = new CategoryPercentOffRule(Category.BOOKS, 10).applyTo(afterFreeUnits);

        assertThat(result.discounts().getLast()).isEqualTo(new Discount("10% off BOOKS", Money.of("25.00")));
        assertThat(result.merchandise()).isEqualTo(Money.of("225.00"));
    }

    @Test
    void amountOffIsCappedAtWhatIsLeftAndNeedsTheThreshold() {
        PriceSheet small = sheet(line("HOM-001", Category.HOME, ProductType.PHYSICAL, 1, "89.90"));

        assertThat(new AmountOffOverRule(Money.of("1.00"), Money.of("1000.00")).applyTo(small).merchandise())
                .isEqualTo(Money.ZERO);
        assertThat(new AmountOffOverRule(Money.of("89.91"), Money.of("10.00")).applyTo(small)).isEqualTo(small);
    }

    @Test
    void couponRoundsHalfEvenOnceAndIsValidThroughItsLastDay() {
        CouponRule coupon = new CouponRule("AUTUMN5", 5, LocalDate.of(2026, 12, 31));

        PriceSheet result = coupon.applyTo(sheet(line("ELE-001", Category.ELECTRONICS, ProductType.PHYSICAL, 1,
                "1030.10")));

        assertThat(result.discounts()).as("5% of 1030.10 = 51.505 → 51.50 (HALF_EVEN; half-up would give 51.51)")
                .containsExactly(new Discount("coupon AUTUMN5 5%", Money.of("51.50")));
        assertThat(coupon.isValidOn(LocalDate.of(2026, 12, 31))).isTrue();
        assertThat(coupon.isValidOn(LocalDate.of(2027, 1, 1))).isFalse();
    }
}
