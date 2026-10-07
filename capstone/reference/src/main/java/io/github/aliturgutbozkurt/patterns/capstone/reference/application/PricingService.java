package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.Adjustment;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PriceQuote;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.AmountOffOver;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.BuyXGetYFree;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.CategoryPercentOff;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec.Coupon;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.QuoteLine;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.CartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.Cart;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartItem;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.AmountOffOverRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.Basket;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.BasketLine;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.BuyXGetYFreeRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CategoryPercentOffRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PriceSheet;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PricingPipeline;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.PromotionRule;
import java.time.Clock;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Feature F4: registers promotions and prices carts through the {@link PricingPipeline}.
 *
 * @see "capstone guide §2 Slice walkthrough — C4"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "application service behind an inbound port")
public final class PricingService implements PricingUseCase {

    private final PromotionRepository promotions;
    private final CartRepository carts;
    private final ProductRepository products;
    private final Clock clock;

    public PricingService(PromotionRepository promotions, CartRepository carts, ProductRepository products,
                          Clock clock) {
        this.promotions = Objects.requireNonNull(promotions, "promotions");
        this.carts = Objects.requireNonNull(carts, "carts");
        this.products = Objects.requireNonNull(products, "products");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public void addPromotion(PromotionSpec promotion) {
        promotions.add(toRule(promotion));
    }

    @Override
    public PriceQuote quote(CartId cart) {
        PriceSheet sheet = priceOf(carts.find(cart)
                .orElseThrow(() -> new NoSuchElementException("unknown cart: " + cart.value())));
        return new PriceQuote(
                sheet.lines().stream().map(line -> new QuoteLine(line.item().sku(), line.item().name(),
                        line.item().quantity(), line.item().unitPrice(), line.lineTotal())).toList(),
                sheet.subtotal(),
                sheet.discounts().stream().map(d -> new Adjustment(d.label(), d.amount())).toList(),
                sheet.shipping(),
                sheet.total());
    }

    /** The price sheet of a cart today, with the current promotions (also what checkout charges). */
    public PriceSheet priceOf(Cart cart) {
        Basket basket = new Basket(cart.items().stream().map(this::basketLine).toList(), cart.coupon(),
                LocalDate.now(clock));
        return PricingPipeline.standard(promotions.all()).price(basket);
    }

    /** Whether the cart's coupon is past its last valid day today (an expired coupon fails checkout). */
    public boolean couponExpired(Cart cart) {
        return !cart.coupon().isEmpty() && promotions.coupon(cart.coupon())
                .map(coupon -> !coupon.isValidOn(LocalDate.now(clock))).orElse(false);
    }

    private BasketLine basketLine(CartItem item) {
        Product product = products.find(item.sku())
                .orElseThrow(() -> new IllegalStateException("product vanished: " + item.sku().value()));
        return new BasketLine(product.sku(), product.name(), product.category(), product.type(), item.quantity(),
                product.price());
    }

    /** The domain rule for a GIVEN promotion spec (exhaustive over the sealed spec, no default). */
    static PromotionRule toRule(PromotionSpec promotion) {
        return switch (promotion) {
            case BuyXGetYFree(var sku, var buy, var free) -> new BuyXGetYFreeRule(sku, buy, free);
            case CategoryPercentOff(var category, var percent) -> new CategoryPercentOffRule(category, percent);
            case AmountOffOver(var threshold, var off) -> new AmountOffOverRule(threshold, off);
            case Coupon(var code, var percent, var validUntil) -> new CouponRule(code, percent, validUntil);
        };
    }
}
