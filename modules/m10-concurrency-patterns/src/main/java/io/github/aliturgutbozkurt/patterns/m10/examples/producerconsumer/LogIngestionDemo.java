package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer;

import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs.LevelCounts;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs.LogIngestion;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/producerconsumer/LogIngestionDemo.java}
 *
 * <p>Which consumer counts which line changes from run to run; the merged counts never do.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public final class LogIngestionDemo {

    private LogIngestionDemo() {}

    public static void main(String[] args) throws InterruptedException {
        List<String> lines = IntStream.range(0, 10_000).mapToObj(LogIngestionDemo::line).toList();
        System.out.println("1 producer, 4 consumers, queue capacity 16, " + lines.size() + " lines");

        LevelCounts counts = LogIngestion.withBoundedQueue(4, 16).ingest(lines);

        System.out.println(counts.counts());
        System.out.println("every line counted once: " + (counts.total() == lines.size()));
        System.out.println("same as a sequential count: " + counts.equals(LevelCounts.countSequentially(lines)));
    }

    /** A deterministic fake log: per 20 lines 1 ERROR, 3 WARN, 12 INFO, 3 DEBUG and 1 corrupted line. */
    private static String line(int n) {
        return switch (n % 20) {
            case 0 -> "ERROR payment " + n + " declined";
            case 1, 2, 3 -> "WARN slow response for order " + n;
            case 16, 17, 18 -> "DEBUG cache miss " + n;
            case 19 -> "#!corrupted line " + n;
            default -> "INFO order " + n + " created";
        };
    }
}
