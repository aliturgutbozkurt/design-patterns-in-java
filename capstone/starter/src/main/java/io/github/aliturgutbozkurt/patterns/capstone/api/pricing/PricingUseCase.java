package io.github.aliturgutbozkurt.patterns.capstone.api.pricing;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import java.util.NoSuchElementException;

/**
 * GIVEN — do not modify. Inbound port of feature F4: promotions and price quotes.
 *
 * @see "capstone brief, Business rules — Pricing (F4)"
 */
public interface PricingUseCase {

    /** Registers a promotion. Promotions of the same kind apply in registration order. */
    void addPromotion(PromotionSpec promotion);

    /**
     * Prices a cart (open or closed) with today's date from the clock, following the fixed order of the brief's
     * Business rules. An unknown cart throws {@link NoSuchElementException}.
     */
    PriceQuote quote(CartId cart);
}
