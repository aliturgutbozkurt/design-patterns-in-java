package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling.BlockingJob;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling.InFlightTracker;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling.ThreadKinds;
import io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.scaling.Workloads;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Timeout(10)
class ThreadPerTaskTest {

    @ParameterizedTest(name = "{0} threads, {1} tasks -> peak {2}")
    @CsvSource({"4, 200, 4", "16, 1000, 16", "8, 3, 3"})
    void fixedPoolCapsInFlightTasksAtThePoolSize(int threads, int tasks, int expectedPeak) {
        var result = Workloads.runOnFixedPool(threads, tasks);
        assertThat(result.peakInFlight()).isEqualTo(expectedPeak).isLessThanOrEqualTo(threads);
        assertThat(result.completed()).isEqualTo(tasks);
    }

    @Test
    void threadPerTaskHasAllTenThousandTasksInFlightAtOnce() {
        // every job waits at a gate that opens only when all 10 000 are running: this cannot pass by luck
        var result = Workloads.runThreadPerTask(10_000);
        assertThat(result.peakInFlight()).isEqualTo(10_000);
        assertThat(result.completed()).isEqualTo(10_000);
    }

    @Test
    void jobThatNeverSeesItsGateOpenFailsInsteadOfHanging() {
        var tracker = new InFlightTracker();
        var job = new BlockingJob(tracker, new CountDownLatch(2), Duration.ofMillis(50));   // 2nd job never comes
        assertThatThrownBy(job::run).isInstanceOf(IllegalStateException.class).hasMessageContaining("gate");
        assertThat(tracker.current()).isZero();
        assertThat(tracker.peak()).isEqualTo(1);
    }

    @Test
    void trackerCountsCurrentPeakAndFinished() {
        var tracker = new InFlightTracker();
        tracker.enter();
        tracker.enter();
        tracker.exit();
        tracker.enter();
        assertThat(tracker.current()).isEqualTo(2);
        assertThat(tracker.peak()).isEqualTo(2);
        assertThat(tracker.finished()).isEqualTo(1);
    }

    @Test
    void virtualThreadsAreDaemonThreadsAndReportIsVirtual() throws InterruptedException {
        var facts = ThreadKinds.describe(Thread.ofVirtual().name("v-", 0));
        assertThat(facts).isEqualTo(new ThreadKinds.Facts("v-0", true, true));
        var platform = ThreadKinds.describe(Thread.ofPlatform().daemon(false).name("p-", 0));
        assertThat(platform).isEqualTo(new ThreadKinds.Facts("p-0", false, false));
    }

    @Test
    void virtualThreadsAreUnnamedByDefault() throws InterruptedException {
        assertThat(ThreadKinds.describe(Thread.ofVirtual()).name()).isEmpty();
    }

    @Test
    void namedFactoryNumbersThreadsInCreationOrder() {
        var factory = ThreadKinds.namedVirtual("crawler-");
        var names = IntStream.range(0, 3).mapToObj(_ -> factory.newThread(() -> {}).getName()).toList();
        assertThat(names).containsExactly("crawler-0", "crawler-1", "crawler-2");
    }

    @Test
    void demoPrintsPeaksAndThreadFacts() {
        assertThat(Demos.output(() -> ThreadPerTaskDemo.main(new String[0]))).isEqualTo("""
                10000 blocking tasks on a fixed pool of 100 platform threads: completed 10000, peak in flight 100
                10000 blocking tasks, one virtual thread each: completed 10000, peak in flight 10000
                Thread.ofPlatform(): Facts[name=worker-0, virtual=false, daemon=false]
                Thread.ofVirtual():  Facts[name=crawler-0, virtual=true, daemon=true]
                unnamed virtual thread has name "": true
                """);
    }
}
