package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.List;
import java.util.Objects;

/**
 * What the customer submits: the items, a coupon code (blank for none) and a card token.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public record CheckoutRequest(List<CartItem> items, String coupon, String card) {

    public CheckoutRequest {
        items = List.copyOf(items);
        Objects.requireNonNull(coupon, "coupon");
        Objects.requireNonNull(card, "card");
    }

    public long totalCents() {
        return items.stream().mapToLong(CartItem::totalCents).sum();
    }
}
