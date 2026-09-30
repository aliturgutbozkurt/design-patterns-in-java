package io.github.aliturgutbozkurt.patterns.m08.examples.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m08.examples.state.jdk.ThreadStates;
import io.github.aliturgutbozkurt.patterns.m08.support.Console;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Timeout(30)
class ThreadStateTest {

    private static final Duration DEADLINE = Duration.ofSeconds(5);

    static Stream<Arguments> builders() {
        return Stream.of(
                Arguments.of("platform", Thread.ofPlatform().daemon(true)),
                Arguments.of("virtual", Thread.ofVirtual()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("builders")
    void unstartedThreadIsNew(String kind, Thread.Builder builder) {
        assertThat(builder.unstarted(() -> {}).getState()).isEqualTo(Thread.State.NEW);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("builders")
    void threadParkedOnALatchIsWaiting(String kind, Thread.Builder builder) throws InterruptedException {
        var gate = new CountDownLatch(1);
        Thread thread = ThreadStates.startParked(builder, gate);
        assertThat(ThreadStates.awaitState(thread, Thread.State.WAITING, DEADLINE)).isEqualTo(Thread.State.WAITING);
        gate.countDown();
        thread.join();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("builders")
    void sleepingThreadIsTimedWaiting(String kind, Thread.Builder builder) throws InterruptedException {
        Thread thread = ThreadStates.startSleeping(builder);
        assertThat(ThreadStates.awaitState(thread, Thread.State.TIMED_WAITING, DEADLINE))
                .isEqualTo(Thread.State.TIMED_WAITING);
        thread.interrupt();
        thread.join();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("builders")
    void joinedThreadIsTerminated(String kind, Thread.Builder builder) throws InterruptedException {
        Thread thread = builder.start(() -> {});
        thread.join();
        assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("builders")
    void lifecycleVisitsTheFourObservableStatesInOrder(String kind, Thread.Builder builder)
            throws InterruptedException {
        assertThat(ThreadStates.lifecycle(builder)).containsExactly(
                Thread.State.NEW, Thread.State.WAITING, Thread.State.TIMED_WAITING, Thread.State.TERMINATED);
    }

    @Test
    void awaitStateGivesUpAfterTheDeadline() {
        Thread unstarted = Thread.ofVirtual().unstarted(() -> {});
        assertThatIllegalStateException()
                .isThrownBy(() -> ThreadStates.awaitState(unstarted, Thread.State.WAITING, Duration.ofMillis(50)))
                .withMessage("thread did not reach WAITING within PT0.05S (still NEW)");
    }

    @Test
    void theJdkDeclaresSixStates() {
        assertThat(Thread.State.values()).containsExactly(Thread.State.NEW, Thread.State.RUNNABLE,
                Thread.State.BLOCKED, Thread.State.WAITING, Thread.State.TIMED_WAITING, Thread.State.TERMINATED);
    }

    @Test
    void demoPrintsTheLifecycleOfAPlatformAndAVirtualThread() {
        String output = Console.capture(() -> {
            try {
                ThreadStateDemo.main(new String[0]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        });
        assertThat(output).isEqualTo("""
                Thread.State: [NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED]
                platform thread: NEW -> WAITING -> TIMED_WAITING -> TERMINATED
                virtual thread:  NEW -> WAITING -> TIMED_WAITING -> TERMINATED
                """);
    }
}
