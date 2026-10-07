package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import static io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingFixtures.DEMO;
import static io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingFixtures.TODAY;
import static io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingFixtures.line;
import static io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingFixtures.workedExample;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingPipelineTest {

    @Test
    void standardPipelinePricesTheWorkedExample() {
        PriceSheet sheet = PricingPipeline.standard(DEMO).price(new Basket(workedExample(), "AUTUMN5", TODAY));

        assertThat(sheet.discounts()).extracting(Discount::amount).containsExactly(
                Money.of("120.00"), Money.of("99.99"), Money.of("100.00"), Money.of("52.00"));
        assertThat(sheet.shipping()).isEqualTo(Money.ZERO);
        assertThat(sheet.total()).isEqualTo(Money.of("987.91"));
    }

    @Test
    void eachDecoratorAddsExactlyItsStep() {
        Basket basket = new Basket(workedExample(), "AUTUMN5", TODAY);
        PriceStep base = new BasePrices();
        PriceStep lines = new LinePromotions(base, DEMO);
        PriceStep order = new OrderPromotion(lines, DEMO);

        assertThat(base.price(basket).discounts()).isEmpty();
        assertThat(lines.price(basket).merchandise()).isEqualTo(Money.of("1139.91"));
        assertThat(order.price(basket).merchandise()).isEqualTo(Money.of("1039.91"));
        assertThat(new CouponDiscount(order, DEMO).price(basket).merchandise()).isEqualTo(Money.of("987.91"));
    }

    @Test
    void decoratorsCanBeLeftOutOrReordered() {
        Basket basket = new Basket(List.of(line("HOM-001", Category.HOME, ProductType.PHYSICAL, 1, "89.90")), "",
                TODAY);

        assertThat(new Shipping(new BasePrices()).price(basket).total()).isEqualTo(Money.of("139.80"));
        assertThat(new BasePrices().price(basket).total()).as("without the shipping decorator")
                .isEqualTo(Money.of("89.90"));
    }

    @Test
    void expiredOrUnknownCouponGivesNothing() {
        Basket expired = new Basket(workedExample(), "AUTUMN5", TODAY.plusYears(1));
        Basket unknown = new Basket(workedExample(), "NOPE", TODAY);

        assertThat(PricingPipeline.standard(DEMO).price(expired).total()).isEqualTo(Money.of("1039.91"));
        assertThat(PricingPipeline.standard(DEMO).price(unknown).total()).isEqualTo(Money.of("1039.91"));
    }
}
