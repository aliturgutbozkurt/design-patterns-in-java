package io.github.aliturgutbozkurt.patterns.m03.examples.builder.classic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Classic Builder: one required part (the size) goes into {@link #builder(Size)}, optional parts are added fluently,
 * and {@link Builder#build()} validates the whole combination before an immutable {@code Pizza} exists.
 *
 * @see "m03 lesson, section Builder"
 */
public final class Pizza extends MenuItem {

    /** Size with its base price. */
    public enum Size {
        SMALL("8.00"), MEDIUM("10.00"), LARGE("13.00");

        private final BigDecimal basePrice;

        Size(String basePrice) {
            this.basePrice = new BigDecimal(basePrice);
        }
    }

    /** Crust; stuffed costs extra. */
    public enum Crust { THIN, CLASSIC, STUFFED }

    private static final int MAX_TOPPINGS = 5;
    private static final BigDecimal TOPPING_PRICE = new BigDecimal("1.50");
    private static final BigDecimal EXTRA_CHEESE_PRICE = new BigDecimal("1.00");
    private static final BigDecimal STUFFED_CRUST_PRICE = new BigDecimal("2.00");

    private final Size size;
    private final Crust crust;
    private final List<String> toppings;
    private final boolean extraCheese;

    private Pizza(Builder builder) {
        // Flexible constructor body (JEP 513): before super(...) we may compute with locals and *assign* our fields,
        // but not *read* them — so the price is computed from the builder's values.
        List<String> chosenToppings = List.copyOf(builder.toppings);
        BigDecimal price = builder.size.basePrice
                .add(TOPPING_PRICE.multiply(BigDecimal.valueOf(chosenToppings.size())))
                .add(builder.extraCheese ? EXTRA_CHEESE_PRICE : BigDecimal.ZERO)
                .add(builder.crust == Crust.STUFFED ? STUFFED_CRUST_PRICE : BigDecimal.ZERO);
        size = builder.size;
        crust = builder.crust;
        toppings = chosenToppings;
        extraCheese = builder.extraCheese;
        super(builder.size.name().toLowerCase(Locale.ROOT) + " pizza", price);
    }

    /** Starts a builder; the size is the only required part. */
    public static Builder builder(Size size) {
        return new Builder(size);
    }

    public Size size() {
        return size;
    }

    public Crust crust() {
        return crust;
    }

    public List<String> toppings() {
        return toppings;
    }

    public boolean extraCheese() {
        return extraCheese;
    }

    /** E.g. {@code "large pizza, stuffed crust, mushroom + olives, extra cheese: 19.00"}. */
    public String describe() {
        return name() + ", " + crust.name().toLowerCase(Locale.ROOT) + " crust, "
                + (toppings.isEmpty() ? "no toppings" : String.join(" + ", toppings))
                + (extraCheese ? ", extra cheese" : "") + ": " + price();
    }

    /**
     * Collects the parts; mutable and not thread-safe, like every builder.
     *
     * @see "m03 lesson, section Builder"
     */
    public static final class Builder {

        private final Size size;
        private Crust crust = Crust.CLASSIC;
        private final List<String> toppings = new ArrayList<>();
        private boolean extraCheese;

        private Builder(Size size) {
            this.size = Objects.requireNonNull(size, "size");
        }

        public Builder crust(Crust crust) {
            this.crust = Objects.requireNonNull(crust, "crust");
            return this;
        }

        public Builder topping(String topping) {
            toppings.add(Objects.requireNonNull(topping, "topping"));
            return this;
        }

        public Builder extraCheese() {
            extraCheese = true;
            return this;
        }

        /** Validates the combination and creates the pizza. */
        public Pizza build() {
            if (toppings.size() > MAX_TOPPINGS) {
                throw new IllegalStateException("at most " + MAX_TOPPINGS + " toppings, got " + toppings.size());
            }
            var seen = new HashSet<String>();
            for (String topping : toppings) {
                if (!seen.add(topping)) {
                    throw new IllegalStateException("duplicate topping: " + topping);
                }
            }
            return new Pizza(this);
        }
    }
}
