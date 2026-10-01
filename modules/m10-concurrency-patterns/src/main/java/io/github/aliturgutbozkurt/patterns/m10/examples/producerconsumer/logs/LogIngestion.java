package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Producer–Consumer: one producer reads log lines into a bounded queue, N consumers take them out and count them
 * per level. The bounded queue is the back-pressure: when consumers fall behind, {@code put} blocks the producer
 * instead of letting the queue grow without limit. Shutdown is one poison pill per consumer, after the last line.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public final class LogIngestion {

    private final int consumers;
    private final BlockingQueue<LogMessage> queue;
    private final ThreadFactory threadFactory;

    /** Consumers run on threads from {@code threadFactory}; the queue should be bounded. */
    public LogIngestion(int consumers, BlockingQueue<LogMessage> queue, ThreadFactory threadFactory) {
        if (consumers < 1) {
            throw new IllegalArgumentException("need at least one consumer: " + consumers);
        }
        this.consumers = consumers;
        this.queue = Objects.requireNonNull(queue, "queue");
        this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory");
    }

    /** {@code consumers} virtual threads behind an {@link ArrayBlockingQueue} of {@code capacity}. */
    public static LogIngestion withBoundedQueue(int consumers, int capacity) {
        return new LogIngestion(consumers, new ArrayBlockingQueue<>(capacity), Thread.ofVirtual().factory());
    }

    /** Produces every line (on the calling thread), then waits for the consumers and merges their counts. */
    public LevelCounts ingest(Iterable<String> lines) throws InterruptedException {
        List<Future<LevelCounts>> results = new ArrayList<>();
        try (var executor = Executors.newThreadPerTaskExecutor(threadFactory)) {
            for (int i = 0; i < consumers; i++) {
                results.add(executor.submit(this::consume));
            }
            for (String line : lines) {
                queue.put(new LogMessage.LogLine(line));     // blocks while the queue is full: back-pressure
            }
            for (int i = 0; i < consumers; i++) {
                queue.put(new LogMessage.EndOfStream());      // one pill per consumer
            }
        }                                                     // close() waits until every consumer has stopped
        LevelCounts total = LevelCounts.fromTally(Map.of());
        for (Future<LevelCounts> partial : results) {
            if (partial.state() == Future.State.FAILED) {
                throw new IllegalStateException("a consumer failed", partial.exceptionNow());
            }
            total = total.merge(partial.resultNow());
        }
        return total;
    }

    private LevelCounts consume() throws InterruptedException {
        Map<Level, Long> tally = new EnumMap<>(Level.class);    // private to this consumer: no locking
        while (true) {
            switch (queue.take()) {
                case LogMessage.LogLine(String text) -> tally.merge(Level.of(text), 1L, Long::sum);
                case LogMessage.EndOfStream() -> {
                    return LevelCounts.fromTally(tally);
                }
            }
        }
    }
}
