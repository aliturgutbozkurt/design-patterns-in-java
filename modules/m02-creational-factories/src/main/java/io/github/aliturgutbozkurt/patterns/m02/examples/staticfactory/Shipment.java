package io.github.aliturgutbozkurt.patterns.m02.examples.staticfactory;

import java.math.BigDecimal;

/**
 * A static factory may return a <em>subtype</em> chosen from its input. Callers ask for "a shipment of 2500 g" and
 * work with {@code Shipment}; which record they get is the factory's decision — like {@code EnumSet.of} or
 * {@code List.of}.
 *
 * @see "m02 lesson, section Static Factory Method"
 */
public sealed interface Shipment {

    int LETTER_LIMIT_GRAMS = 500;
    int PARCEL_LIMIT_GRAMS = 30_000;

    int grams();

    BigDecimal price();

    /** Letter up to 500 g, parcel up to 30 kg, freight above. */
    static Shipment forWeight(int grams) {
        if (grams <= 0) {
            throw new IllegalArgumentException("weight must be positive: " + grams + " g");
        }
        if (grams <= LETTER_LIMIT_GRAMS) {
            return new Letter(grams);
        }
        return grams <= PARCEL_LIMIT_GRAMS ? new Parcel(grams) : new Freight(grams);
    }

    /** Flat 2.50. */
    record Letter(int grams) implements Shipment {
        @Override
        public BigDecimal price() {
            return new BigDecimal("2.50");
        }
    }

    /** 6.00 plus 0.50 per started kilogram. */
    record Parcel(int grams) implements Shipment {
        @Override
        public BigDecimal price() {
            return new BigDecimal("6.00").add(new BigDecimal("0.50").multiply(BigDecimal.valueOf(startedKilograms(grams))));
        }
    }

    /** 40.00 plus 0.30 per started kilogram. */
    record Freight(int grams) implements Shipment {
        @Override
        public BigDecimal price() {
            return new BigDecimal("40.00").add(new BigDecimal("0.30").multiply(BigDecimal.valueOf(startedKilograms(grams))));
        }
    }

    private static int startedKilograms(int grams) {
        return (grams + 999) / 1000;
    }
}
