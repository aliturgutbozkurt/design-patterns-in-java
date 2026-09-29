package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.thermometer;

/**
 * Adaptee: a legacy sensor driver that only speaks Fahrenheit. This fake replays fixed readings in a loop.
 *
 * @see "m04 lesson, section Adapter"
 */
public class FahrenheitSensor {

    private final double[] readings;
    private int reads;

    public FahrenheitSensor(double... readings) {
        if (readings.length == 0) {
            throw new IllegalArgumentException("at least one reading is required");
        }
        this.readings = readings.clone();
    }

    /** The legacy API: the current temperature in degrees Fahrenheit. */
    public double readFahrenheit() {
        return readings[reads++ % readings.length];
    }

    /** How often the sensor has been read (lets tests see that an adapter does not cache). */
    public int readCount() {
        return reads;
    }
}
