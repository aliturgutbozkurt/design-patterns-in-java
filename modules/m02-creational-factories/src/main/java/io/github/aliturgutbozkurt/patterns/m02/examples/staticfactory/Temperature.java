package io.github.aliturgutbozkurt.patterns.m02.examples.staticfactory;

/**
 * Three ways to create a temperature, all taking one {@code double}. Overloaded constructors cannot tell them apart;
 * named static factories can.
 *
 * @see "m02 lesson, section Static Factory Method"
 */
public final class Temperature {

    private static final double ZERO_CELSIUS_IN_KELVIN = 273.15;

    private final double kelvin;

    private Temperature(double kelvin) {
        if (!(kelvin >= 0)) {
            throw new IllegalArgumentException("below absolute zero: " + kelvin + " K");
        }
        this.kelvin = kelvin;
    }

    public static Temperature ofKelvin(double kelvin) {
        return new Temperature(kelvin);
    }

    public static Temperature ofCelsius(double celsius) {
        return new Temperature(celsius + ZERO_CELSIUS_IN_KELVIN);
    }

    public static Temperature ofFahrenheit(double fahrenheit) {
        return ofCelsius((fahrenheit - 32) * 5 / 9);
    }

    public double kelvin() {
        return kelvin;
    }

    public double celsius() {
        return kelvin - ZERO_CELSIUS_IN_KELVIN;
    }

    public double fahrenheit() {
        return celsius() * 9 / 5 + 32;
    }
}
