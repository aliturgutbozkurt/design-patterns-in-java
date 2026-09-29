package io.github.aliturgutbozkurt.patterns.m02.examples.staticfactory;

import java.util.List;
import java.util.Locale;

/** Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/staticfactory/StaticFactoryDemo.java} */
public final class StaticFactoryDemo {

    private StaticFactoryDemo() {}

    public static void main(String[] args) {
        System.out.println("== named factories ==");
        System.out.println("Money.of(\"12.5\", \"EUR\") = " + Money.of("12.5", "EUR"));
        System.out.println("Money.parse(\"99.90 TRY\") = " + Money.parse("99.90 TRY"));
        System.out.println("Money.zero(\"USD\") = " + Money.zero("USD"));
        System.out.println("ofCelsius(100) = " + oneDecimal(Temperature.ofCelsius(100).fahrenheit()) + " °F");
        System.out.println("ofFahrenheit(212) = " + oneDecimal(Temperature.ofFahrenheit(212).celsius()) + " °C");

        System.out.println("== instance caching ==");
        System.out.println("Percentage.of(15) == Percentage.of(15)? " + (Percentage.of(15) == Percentage.of(15)));

        System.out.println("== choosing a subtype ==");
        for (int grams : List.of(300, 2_500, 45_000)) {
            Shipment shipment = Shipment.forWeight(grams);
            System.out.println(grams + " g -> " + shipment.getClass().getSimpleName() + " " + shipment.price());
        }
    }

    private static String oneDecimal(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
