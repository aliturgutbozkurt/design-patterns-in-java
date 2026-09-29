package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.gatherers;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * Sensor analytics with the built-in gatherers. Each one looks at more than one element at a time, which
 * {@code map} and {@code filter} cannot do.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class ReadingAnalytics {

    private ReadingAnalytics() {}

    /**
     * The average of every {@code window} consecutive values. A stream shorter than the window gives
     * <em>one</em> partial window, so one average of the values there are.
     */
    public static List<Double> movingAverages(List<SensorReading> readings, int window) {
        return readings.stream()
                .map(SensorReading::value)
                .gather(Gatherers.windowSliding(window))
                .map(values -> values.stream().mapToDouble(Double::doubleValue).average().orElseThrow())
                .toList();
    }

    /** The values in batches of {@code size}; the last batch may be shorter. The batches are unmodifiable. */
    public static List<List<Double>> batches(List<SensorReading> readings, int size) {
        return readings.stream()
                .map(SensorReading::value)
                .gather(Gatherers.windowFixed(size))
                .toList();
    }

    /** The running total after each reading. */
    public static List<Double> runningTotals(List<SensorReading> readings) {
        return readings.stream()
                .map(SensorReading::value)
                .gather(Gatherers.scan(() -> 0.0, Double::sum))
                .toList();
    }
}
