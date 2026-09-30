package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs.Level;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs.LevelCounts;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs.LogIngestion;
import io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.logs.LogMessage;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(10)
class LogIngestionTest {

    /** Records the largest size it ever had and how many poison pills were taken out of it. */
    static final class InstrumentedQueue extends ArrayBlockingQueue<LogMessage> {
        private static final long serialVersionUID = 1L;
        final AtomicInteger maxSize = new AtomicInteger();
        final AtomicInteger pillsTaken = new AtomicInteger();

        InstrumentedQueue(int capacity) {
            super(capacity);
        }

        @Override
        public void put(LogMessage message) throws InterruptedException {
            super.put(message);
            maxSize.accumulateAndGet(size(), Math::max);
        }

        @Override
        public LogMessage take() throws InterruptedException {
            LogMessage message = super.take();
            if (message instanceof LogMessage.EndOfStream) {
                pillsTaken.incrementAndGet();
            }
            return message;
        }
    }

    private static List<String> lines(int count) {
        String[] levels = {"ERROR", "WARN", "INFO", "INFO", "DEBUG", "INFO", "WARN"};
        return IntStream.range(0, count).mapToObj(i -> levels[i % levels.length] + " event " + i).toList();
    }

    @ParameterizedTest(name = "{0} consumer(s)")
    @ValueSource(ints = {1, 2, 8})
    void countsEveryLineExactlyOnce(int consumers) throws InterruptedException {
        var input = lines(5_000);
        LevelCounts counts = LogIngestion.withBoundedQueue(consumers, 16).ingest(input);
        assertThat(counts.total()).isEqualTo(5_000);
        assertThat(counts).isEqualTo(LevelCounts.countSequentially(input));
    }

    @Test
    void boundedQueueNeverHoldsMoreThanItsCapacity() throws InterruptedException {
        var queue = new InstrumentedQueue(2);
        new LogIngestion(3, queue, Thread.ofVirtual().factory()).ingest(lines(1_000));
        assertThat(queue.maxSize.get()).isBetween(1, 2);
    }

    @Test
    void everyConsumerGetsItsOwnPoisonPillAndTerminates() throws InterruptedException {
        var queue = new InstrumentedQueue(4);
        List<Thread> consumers = new CopyOnWriteArrayList<>();
        ThreadFactory recording = task -> {
            Thread thread = Thread.ofVirtual().unstarted(task);
            consumers.add(thread);
            return thread;
        };
        new LogIngestion(5, queue, recording).ingest(lines(100));
        assertThat(queue.pillsTaken.get()).isEqualTo(5);
        assertThat(consumers).hasSize(5).allMatch(thread -> thread.getState() == Thread.State.TERMINATED);
    }

    @Test
    void malformedLineIsCountedAsUnparseableInsteadOfThrown() throws InterruptedException {
        var counts = LogIngestion.withBoundedQueue(2, 4).ingest(List.of("ERROR disk full", "%%%", "", "info lower"));
        assertThat(counts.of(Level.ERROR)).isEqualTo(1);
        assertThat(counts.of(Level.UNPARSEABLE)).isEqualTo(3);
    }

    @Test
    void emptyInputGivesZeroCountsForEveryLevel() throws InterruptedException {
        var counts = LogIngestion.withBoundedQueue(3, 4).ingest(List.of());
        assertThat(counts.total()).isZero();
        assertThat(counts.counts()).containsOnlyKeys(Level.values()).containsValues(0L);
    }

    @Test
    void levelCountsAreUnmodifiableAndMerge() {
        var a = LevelCounts.countSequentially(List.of("ERROR a", "WARN b"));
        var b = LevelCounts.countSequentially(List.of("ERROR c"));
        assertThat(a.merge(b).of(Level.ERROR)).isEqualTo(2);
        assertThatThrownBy(() -> a.counts().put(Level.INFO, 9L)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsFewerThanOneConsumer() {
        assertThatThrownBy(() -> LogIngestion.withBoundedQueue(0, 4)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void demoPrintsCountsPerLevel() {
        assertThat(Demos.output(() -> LogIngestionDemo.main(new String[0]))).isEqualTo("""
                1 producer, 4 consumers, queue capacity 16, 10000 lines
                {ERROR=500, WARN=1500, INFO=6000, DEBUG=1500, UNPARSEABLE=500}
                every line counted once: true
                same as a sequential count: true
                """);
    }
}
