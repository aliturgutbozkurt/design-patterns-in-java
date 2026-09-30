package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.modern;

import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.Parcel;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/strategy/shipping/modern/ShippingDemo.java} */
public final class ShippingDemo {

    private ShippingDemo() {}

    public static void main(String[] args) {
        ShippingRule cheapest = ShippingRules.cheapestOf(ShippingOption.values());
        List<Parcel> parcels = List.of(
                new Parcel(0.5, new BigDecimal("20.00")),
                new Parcel(12.0, new BigDecimal("45.00")),
                new Parcel(3.0, new BigDecimal("50.00")));
        for (Parcel parcel : parcels) {
            String label = "parcel " + parcel.weightKg() + " kg, order " + parcel.orderTotal() + ":";
            String quotes = Arrays.stream(ShippingOption.values())
                    .map(option -> option.name() + " " + option.cost(parcel))
                    .collect(Collectors.joining(" | "));
            System.out.println(String.format("%-29s", label) + quotes + " | cheapest " + cheapest.cost(parcel));
        }

        // A one-off strategy needs no new class: a lambda is enough.
        ShippingRule express = ShippingRules.weightBased(new BigDecimal("9.90"), new BigDecimal("1.00"));
        System.out.println("express (lambda, 9.90 + 1.00/kg) for 3.0 kg: " + express.cost(parcels.get(2)));
    }
}
