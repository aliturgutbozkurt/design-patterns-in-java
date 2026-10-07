package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * What is priced: the lines, the applied coupon code ({@code ""} when none) and the day of pricing.
 *
 * @param lines  the lines in cart order
 * @param coupon the coupon code or {@code ""}
 * @param today  the clock's date (decides whether the coupon is still valid)
 * @see "capstone guide, Slice walkthrough — C4"
 */
public record Basket(List<BasketLine> lines, String coupon, LocalDate today) {

    public Basket {
        lines = List.copyOf(lines);
        Objects.requireNonNull(coupon, "coupon");
        Objects.requireNonNull(today, "today");
    }
}
