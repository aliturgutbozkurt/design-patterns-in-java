package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.thermometer;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/adapter/thermometer/ThermometerAdapterDemo.java} */
public final class ThermometerAdapterDemo {

    private ThermometerAdapterDemo() {}

    public static void main(String[] args) {
        CelsiusThermometer adapterClass = new FahrenheitAdapter(new FahrenheitSensor(212.0, 32.0, -40.0));

        FahrenheitSensor sensor = new FahrenheitSensor(212.0, 32.0, -40.0);
        CelsiusThermometer lambda = () -> FahrenheitAdapter.toCelsius(sensor.readFahrenheit());

        System.out.println("adapter class:  " + threeReadings(adapterClass));
        System.out.println("lambda adapter: " + threeReadings(lambda));
    }

    private static String threeReadings(CelsiusThermometer thermometer) {
        return Stream.generate(thermometer::readCelsius)
                .limit(3)
                .map(celsius -> String.format(Locale.ROOT, "%.1f °C", celsius))
                .collect(Collectors.joining(", "));
    }
}
