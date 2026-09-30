package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

import java.util.Map;
import java.util.Optional;

/**
 * The known coupon codes and their discount in percent.
 *
 * @see "m09 lesson, section Optional and Result — choosing an error model"
 */
public final class Coupons {

    private static final Map<String, Integer> PERCENT_OFF = Map.of("SAVE10", 10, "HALF", 50);

    private Coupons() {}

    /** The discount for {@code code}, or empty if the code is unknown. */
    public static Optional<Integer> percentOff(String code) {
        return Optional.ofNullable(PERCENT_OFF.get(code));
    }

    /** {@code total} minus {@code percent} %, rounded down to whole cents. */
    public static long discounted(long totalCents, int percent) {
        return totalCents * (100 - percent) / 100;
    }
}
