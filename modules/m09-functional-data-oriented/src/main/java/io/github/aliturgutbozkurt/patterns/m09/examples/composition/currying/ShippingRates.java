package io.github.aliturgutbozkurt.patterns.m09.examples.composition.currying;

import java.util.function.Function;

/**
 * A shipping tariff as a curried function: zone → weight in grams → price in cents. Fixing the zone gives a
 * "configured strategy" ({@code Function<Integer, Long>}) without writing a class for it.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class ShippingRates {

    /** Where the parcel goes; each zone has a base price for the first kilogram and a price per extra kilogram. */
    public enum Zone {
        DOMESTIC(4_99, 1_00), EU(9_99, 2_50), WORLD(19_99, 5_00);

        private final long baseCents;
        private final long perExtraKgCents;

        Zone(long baseCents, long perExtraKgCents) {
            this.baseCents = baseCents;
            this.perExtraKgCents = perExtraKgCents;
        }
    }

    private ShippingRates() {}

    /** The whole tariff, curried. */
    public static Function<Zone, Function<Integer, Long>> rates() {
        return zone -> grams -> {
            if (grams <= 0) {
                throw new IllegalArgumentException("weight must be positive: " + grams + " g");
            }
            long extraKg = (Math.max(0, grams - 1000) + 999) / 1000;
            return zone.baseCents + extraKg * zone.perExtraKgCents;
        };
    }

    /** Partial application: the tariff of one zone, ready to be stored and reused. */
    public static Function<Integer, Long> forZone(Zone zone) {
        return rates().apply(zone);
    }
}
