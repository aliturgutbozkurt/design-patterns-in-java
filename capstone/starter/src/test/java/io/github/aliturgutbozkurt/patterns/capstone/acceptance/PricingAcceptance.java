package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.money;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.Item;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Category;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.ProductType;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.Adjustment;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PriceQuote;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.QuoteLine;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * F4 — pricing and promotions in the fixed order of brief §2.2 (demo promotions: buy 2 get 1 free on TOY-001, 10% off
 * BOOKS, 100.00 off over 1000.00, coupon AUTUMN5 5% until 2026-12-31).
 */
public abstract class PricingAcceptance extends AcceptanceContract {

    private PriceQuote quote(Item... items) {
        return shop().pricing().quote(kit().cartWith("alice", items));
    }

    private PriceQuote quoteWithCoupon(String coupon, Item... items) {
        CartId cart = kit().cartWith("alice", items);
        shop().carts().applyCoupon(cart, coupon);
        return shop().pricing().quote(cart);
    }

    private static Adjustment discount(String label, String amount) {
        return new Adjustment(label, money(amount));
    }

    private void addProduct(String sku, String name, Category category, String price) {
        shop().catalogue().add(new ProductSpec(sku(sku), name, category, ProductType.PHYSICAL, money(price), 100));
    }

    @Test
    void emptyCartQuotesZero() {
        PriceQuote quote = shop().pricing().quote(shop().carts().open(customer("alice")));

        assertThat(quote).isEqualTo(new PriceQuote(List.of(), Money.ZERO, List.of(), Money.ZERO, Money.ZERO));
    }

    @Test
    void subtotalIsSumOfLineTotals() {
        PriceQuote quote = quote(item("HOM-001", 3), item("ELE-001", 1));

        assertThat(quote.lines()).containsExactly(
                new QuoteLine(sku("HOM-001"), "Hexagon Mug", 3, money("89.90"), money("269.70")),
                new QuoteLine(sku("ELE-001"), "USB-C Hub", 1, money("649.00"), money("649.00")));
        assertThat(quote.subtotal()).isEqualTo(money("918.70"));
        assertThat(quote.discounts()).isEmpty();
        assertThat(quote.shipping()).isEqualTo(Money.ZERO);
        assertThat(quote.total()).isEqualTo(money("918.70"));
    }

    @Test
    void buyXGetYFreeDiscountsWholeGroupsOnly() {
        assertThat(quote(item("TOY-001", 2)).discounts()).as("2 units: no complete group of 3").isEmpty();

        PriceQuote five = quote(item("TOY-001", 5));
        assertThat(five.discounts()).containsExactly(discount("buy 2 get 1 free: TOY-001", "120.00"));
        assertThat(five.total()).as("600.00 - 120.00 + shipping 49.90").isEqualTo(money("529.90"));

        assertThat(quote(item("TOY-001", 6)).discounts())
                .containsExactly(discount("buy 2 get 1 free: TOY-001", "240.00"));
    }

    @Test
    void categoryPercentOffRoundsHalfEvenPerLine() {
        addProduct("BOK-101", "Pattern Pamphlet", Category.BOOKS, "0.05");
        addProduct("BOK-102", "Pattern Leaflet", Category.BOOKS, "0.15");
        addProduct("BOK-103", "Pattern Flyer", Category.BOOKS, "0.25");
        addProduct("BOK-104", "Pattern Bookmark", Category.BOOKS, "0.14");

        PriceQuote perLine = quote(item("BOK-101", 1), item("BOK-102", 1), item("BOK-103", 1), item("BOK-104", 1));
        assertThat(perLine.discounts())
                .as("0.005 → 0.00, 0.015 → 0.02, 0.025 → 0.02, 0.014 → 0.01 (HALF_EVEN per line); "
                        + "half-up would give 0.07, half-down 0.04, 10% of the 0.59 total 0.06")
                .containsExactly(discount("10% off BOOKS", "0.05"));

        shop().pricing().addPromotion(new PromotionSpec.BuyXGetYFree(sku("BOK-001"), 1, 1));
        PriceQuote afterStep2 = quote(item("BOK-001", 2), item("TOY-001", 1));
        assertThat(afterStep2.discounts()).as("10% of what is left after buy 1 get 1 free").containsExactly(
                discount("buy 1 get 1 free: BOK-001", "250.00"),
                discount("10% off BOOKS", "25.00"));
    }

    @Test
    void amountOffOverThresholdUsesDiscountedSubtotal() {
        PriceQuote notReached = quote(item("BOK-002", 2), item("TOY-001", 3));
        assertThat(notReached.subtotal()).isEqualTo(money("1160.00"));
        assertThat(notReached.discounts()).as("1160.00 - 120.00 - 80.00 = 960.00 < 1000.00").containsExactly(
                discount("buy 2 get 1 free: TOY-001", "120.00"),
                discount("10% off BOOKS", "80.00"));
        assertThat(notReached.total()).isEqualTo(money("960.00"));

        PriceQuote reached = quote(item("ELE-001", 2));
        assertThat(reached.discounts()).containsExactly(discount("100.00 off over 1000.00", "100.00"));
        assertThat(reached.total()).isEqualTo(money("1198.00"));
    }

    @Test
    void onlyHighestQualifyingThresholdApplies() {
        shop().pricing().addPromotion(new PromotionSpec.AmountOffOver(money("500.00"), money("30.00")));
        shop().pricing().addPromotion(new PromotionSpec.AmountOffOver(money("2000.00"), money("300.00")));

        assertThat(quote(item("ELE-001", 2)).discounts()).as("1298.00 qualifies for 500 and 1000")
                .containsExactly(discount("100.00 off over 1000.00", "100.00"));
        PriceQuote large = quote(item("ELE-001", 4));
        assertThat(large.discounts()).containsExactly(discount("300.00 off over 2000.00", "300.00"));
        assertThat(large.total()).isEqualTo(money("2296.00"));
    }

    @Test
    void couponAppliesLastOnRemainingAmount() {
        PriceQuote quote = quoteWithCoupon("AUTUMN5", item("ELE-001", 2));

        assertThat(quote.discounts()).containsExactly(
                discount("100.00 off over 1000.00", "100.00"),
                discount("coupon AUTUMN5 5%", "59.90"));
        assertThat(quote.total()).as("5% of 1198.00 after the threshold discount").isEqualTo(money("1138.10"));
    }

    @Test
    void expiredCouponGivesNoDiscount() {
        shop().pricing().addPromotion(new PromotionSpec.Coupon("LASTDAY", 10, LocalDate.of(2026, 11, 16)));
        CartId cart = kit().cartWith("alice", item("HOM-001", 10));
        shop().carts().applyCoupon(cart, "LASTDAY");

        assertThat(shop().pricing().quote(cart).discounts()).as("still valid on its last day")
                .containsExactly(discount("coupon LASTDAY 10%", "89.90"));

        kit().clock().advance(Duration.ofDays(1));
        PriceQuote expired = shop().pricing().quote(cart);
        assertThat(expired.discounts()).isEmpty();
        assertThat(expired.total()).isEqualTo(money("899.00"));
    }

    @Test
    void workedExampleFromTheBrief() {
        PriceQuote quote = quoteWithCoupon("AUTUMN5",
                item("BOK-001", 2), item("BOK-002", 1), item("TOY-001", 3), item("DIG-001", 1));

        assertThat(quote.lines()).containsExactly(
                new QuoteLine(sku("BOK-001"), "Design Patterns Handbook", 2, money("250.00"), money("500.00")),
                new QuoteLine(sku("BOK-002"), "Java 27 in Action", 1, money("400.00"), money("400.00")),
                new QuoteLine(sku("TOY-001"), "Pattern Puzzle", 3, money("120.00"), money("360.00")),
                new QuoteLine(sku("DIG-001"), "E-book Bundle", 1, money("99.90"), money("99.90")));
        assertThat(quote.subtotal()).isEqualTo(money("1359.90"));
        assertThat(quote.discounts()).containsExactly(
                discount("buy 2 get 1 free: TOY-001", "120.00"),
                discount("10% off BOOKS", "99.99"),
                discount("100.00 off over 1000.00", "100.00"),
                discount("coupon AUTUMN5 5%", "52.00"));
        assertThat(quote.shipping()).isEqualTo(Money.ZERO);
        assertThat(quote.total()).isEqualTo(money("987.91"));
    }

    @Test
    void shippingFeeBelowThresholdFreeAtOrAbove() {
        addProduct("HOM-101", "Almost Lamp", Category.HOME, "499.99");
        addProduct("HOM-102", "Exact Lamp", Category.HOME, "500.00");

        PriceQuote below = quote(item("HOM-101", 1));
        assertThat(below.shipping()).isEqualTo(money("49.90"));
        assertThat(below.total()).isEqualTo(money("549.89"));

        PriceQuote atThreshold = quote(item("HOM-102", 1));
        assertThat(atThreshold.shipping()).isEqualTo(Money.ZERO);
        assertThat(atThreshold.total()).isEqualTo(money("500.00"));

        PriceQuote discountedBelow = quote(item("BOK-001", 2));
        assertThat(discountedBelow.shipping()).as("500.00 - 10% = 450.00 is below 500.00").isEqualTo(money("49.90"));
        assertThat(discountedBelow.total()).isEqualTo(money("499.90"));
    }

    @Test
    void digitalOnlyCartHasNoShipping() {
        PriceQuote quote = quote(item("DIG-001", 1));

        assertThat(quote.discounts()).containsExactly(discount("10% off BOOKS", "9.99"));
        assertThat(quote.shipping()).isEqualTo(Money.ZERO);
        assertThat(quote.total()).isEqualTo(money("89.91"));
    }

    @Test
    void discountsNeverMakeTheTotalNegative() {
        shop().pricing().addPromotion(new PromotionSpec.AmountOffOver(money("1.00"), money("1000.00")));

        PriceQuote quote = quote(item("HOM-001", 1));

        assertThat(quote.discounts()).as("capped at what is left")
                .containsExactly(discount("1000.00 off over 1.00", "89.90"));
        assertThat(quote.shipping()).isEqualTo(money("49.90"));
        assertThat(quote.total()).as("merchandise 0.00 + shipping").isEqualTo(money("49.90"));
    }
}
