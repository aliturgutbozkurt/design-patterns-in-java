package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/strategy/shipping/classic/ShippingDemo.java} */
public final class ShippingDemo {

    private ShippingDemo() {}

    public static void main(String[] args) {
        ShippingStrategy flat = new FlatRate(new BigDecimal("4.99"));
        ShippingStrategy perKg = new WeightBased(new BigDecimal("2.00"), new BigDecimal("0.80"));
        ShippingStrategy freeOver50 = new FreeOverThreshold(new BigDecimal("50.00"), flat);
        List<ShippingStrategy> strategies = List.of(flat, perKg, freeOver50);

        List<Parcel> parcels = List.of(
                new Parcel(0.5, new BigDecimal("20.00")),
                new Parcel(12.0, new BigDecimal("45.00")),
                new Parcel(3.0, new BigDecimal("50.00")));
        for (Parcel parcel : parcels) {
            String label = "parcel " + parcel.weightKg() + " kg, order " + parcel.orderTotal() + ":";
            String quotes = strategies.stream()
                    .map(strategy -> strategy.name() + " " + strategy.cost(parcel))
                    .collect(Collectors.joining(" | "));
            System.out.println(String.format("%-29s", label) + quotes);
        }

        var checkout = new ShippingCalculator(flat);
        Parcel heavy = parcels.get(1);
        System.out.println("checkout uses " + checkout.strategy().name() + ": " + checkout.quote(heavy));
        checkout.setStrategy(perKg);  // same context, new algorithm
        System.out.println("checkout switched to " + checkout.strategy().name() + ": " + checkout.quote(heavy));
    }
}
