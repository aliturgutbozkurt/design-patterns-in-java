package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.gatherers;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/iterator/gatherers/GatherersDemo.java} */
public final class GatherersDemo {

    private GatherersDemo() {}

    public static void main(String[] args) {
        List<SensorReading> readings = List.of(
                new SensorReading("t1", 0, 10.0),
                new SensorReading("t1", 60, 12.0),
                new SensorReading("t1", 120, 14.0),
                new SensorReading("t1", 180, 13.0),
                new SensorReading("t1", 240, 30.0));
        System.out.println("values:               " + readings.stream().map(r -> String.valueOf(r.value()))
                .collect(Collectors.joining(", ")));
        System.out.println("3-point moving avg:   " + ReadingAnalytics.movingAverages(readings, 3).stream()
                .map(avg -> String.format(Locale.ROOT, "%.2f", avg)).collect(Collectors.joining(", ")));
        System.out.println("windowFixed(2):       " + ReadingAnalytics.batches(readings, 2));
        System.out.println("running total (scan): " + ReadingAnalytics.runningTotals(readings));

        List<Click> clicks = List.of(
                new Click("home", 0), new Click("search", 12), new Click("product", 40),
                new Click("home", 200), new Click("cart", 215),
                new Click("checkout", 600));
        System.out.println("sessions (gap > 30 s): " + clicks.stream().gather(SessionGatherer.sessions(30)).toList());
        System.out.println("clicks so far after each session: " + clicks.stream()
                .gather(SessionGatherer.sessions(30).andThen(Gatherers.scan(() -> 0, (n, s) -> n + s.size())))
                .toList());
    }
}
