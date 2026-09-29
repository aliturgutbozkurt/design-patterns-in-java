package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResilienceTest {

    private static final Map<String, Integer> STOCK = Map.of("A-42", 7);

    @Test
    void flakyServiceFailsTheFirstCallsThenAnswers() {
        var flaky = new FlakyStockService(2, STOCK);
        assertThatIllegalStateException().isThrownBy(() -> flaky.available("A-42")).withMessage("warehouse timeout #1");
        assertThatIllegalStateException().isThrownBy(() -> flaky.available("A-42")).withMessage("warehouse timeout #2");
        assertThat(flaky.available("A-42")).isEqualTo(7);
        assertThat(flaky.available("unknown")).isZero();
        assertThat(flaky.calls()).isEqualTo(4);
    }

    @Test
    void retrySucceedsWhenFailuresAreFewerThanMaxAttempts() {
        var flaky = new FlakyStockService(2, STOCK);
        assertThat(new RetryingStockService(flaky, 3).available("A-42")).isEqualTo(7);
        assertThat(flaky.calls()).isEqualTo(3);
    }

    @Test
    void afterMaxAttemptsTheLastFailureIsRethrownWithEarlierOnesSuppressed() {
        var flaky = new FlakyStockService(5, STOCK);
        var failure = catchThrowableOfType(IllegalStateException.class,
                () -> new RetryingStockService(flaky, 3).available("A-42"));
        assertThat(failure).hasMessage("warehouse timeout #3");
        assertThat(failure.getSuppressed()).extracting(Throwable::getMessage)
                .containsExactly("warehouse timeout #1", "warehouse timeout #2");
        assertThat(flaky.calls()).isEqualTo(3);
    }

    @Test
    void loggingAroundRetryingLogsOneCall() {
        List<String> log = new ArrayList<>();
        StockService service = new LoggingStockService(
                new RetryingStockService(new FlakyStockService(2, STOCK), 3), log::add);
        service.available("A-42");
        assertThat(log).containsExactly("available(A-42) = 7");
    }

    @Test
    void retryingAroundLoggingLogsEveryAttempt() {
        List<String> log = new ArrayList<>();
        StockService service = new RetryingStockService(
                new LoggingStockService(new FlakyStockService(2, STOCK), log::add), 3);
        service.available("A-42");
        assertThat(log).containsExactly(
                "available(A-42) failed: warehouse timeout #1",
                "available(A-42) failed: warehouse timeout #2",
                "available(A-42) = 7");
    }

    @Test
    void loggingRethrowsTheFailureUnchanged() {
        List<String> log = new ArrayList<>();
        StockService service = new LoggingStockService(new FlakyStockService(1, STOCK), log::add);
        assertThatIllegalStateException().isThrownBy(() -> service.available("A-42"))
                .withMessage("warehouse timeout #1");
        assertThat(log).containsExactly("available(A-42) failed: warehouse timeout #1");
    }

    @Test
    void rejectsInvalidConfiguration() {
        var flaky = new FlakyStockService(0, STOCK);
        assertThatIllegalArgumentException().isThrownBy(() -> new RetryingStockService(flaky, 0))
                .withMessage("maxAttempts must be at least 1: 0");
        assertThatNullPointerException().isThrownBy(() -> new RetryingStockService(null, 3));
        assertThatNullPointerException().isThrownBy(() -> new LoggingStockService(flaky, null));
        assertThatIllegalArgumentException().isThrownBy(() -> new FlakyStockService(-1, STOCK));
    }

    @Test
    void demoPrintsBothStackingOrders() {
        assertThat(Console.capture(() -> ResilienceDemo.main(new String[0]))).isEqualTo("""
                logging(retrying(flaky)):
                  log: available(A-42) = 7
                retrying(logging(flaky)):
                  log: available(A-42) failed: warehouse timeout #1
                  log: available(A-42) failed: warehouse timeout #2
                  log: available(A-42) = 7
                retrying(flaky) that never recovers:
                  gave up: warehouse timeout #3 (suppressed: warehouse timeout #1, warehouse timeout #2)
                """);
    }
}
