package io.github.aliturgutbozkurt.patterns.m00.exercises.ex01;

/** GIVEN — do not modify. Temperature units. */
public enum Unit {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    KELVIN("K");

    private final String symbol;

    Unit(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
