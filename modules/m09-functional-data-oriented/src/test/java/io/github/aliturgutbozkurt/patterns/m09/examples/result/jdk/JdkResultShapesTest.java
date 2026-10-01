package io.github.aliturgutbozkurt.patterns.m09.examples.result.jdk;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Err;
import io.github.aliturgutbozkurt.patterns.m09.examples.result.core.Result.Ok;
import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class JdkResultShapesTest {

    private final JdkResultShapes shapes = JdkResultShapes.sample();

    @Test
    void thenApplyIsMapAndThenComposeIsFlatMap() {
        int joined = CompletableFuture.completedFuture(2)
                .thenApply(x -> x * 10)
                .thenCompose(x -> CompletableFuture.completedFuture(x + 1))
                .join();
        assertThat(joined).isEqualTo(21);
    }

    @Test
    void thenComposeOnAFailedFutureNeverCallsItsFunction() {
        var calls = new AtomicInteger();
        CompletableFuture<Integer> failed = CompletableFuture.failedFuture(new IllegalStateException("down"));
        var next = failed.thenCompose(x -> {
            calls.incrementAndGet();
            return CompletableFuture.completedFuture(x + 1);
        });
        assertThat(next).isCompletedExceptionally();
        assertThat(calls).hasValue(0);
    }

    @Test
    void exceptionallySeesTheRawExceptionOnlyDirectlyOnTheFailedFuture() {
        var cause = new IllegalStateException("down");
        var direct = new AtomicReference<Throwable>();
        var afterStage = new AtomicReference<Throwable>();
        CompletableFuture.<Integer>failedFuture(cause).exceptionally(e -> {
            direct.set(e);
            return 0;
        }).join();
        CompletableFuture.<Integer>failedFuture(cause).thenApply(x -> x + 1).exceptionally(e -> {
            afterStage.set(e);
            return 0;
        }).join();
        assertThat(direct.get()).isSameAs(cause);
        assertThat(afterStage.get()).isInstanceOf(CompletionException.class).hasCause(cause);
    }

    @Test
    void toResultGivesOkOrTheUnwrappedCause() {
        var cause = new IllegalStateException("down");
        assertThat(FutureResults.toResult(CompletableFuture.completedFuture(7))).isEqualTo(new Ok<>(7));
        Result<Integer, Throwable> failed =
                FutureResults.toResult(CompletableFuture.<Integer>failedFuture(cause).thenApply(x -> x + 1));
        assertThat(failed).isEqualTo(new Err<>(cause));
        assertThat(FutureResults.toResult(CompletableFuture.<Integer>failedFuture(cause))).isEqualTo(new Err<>(cause));
    }

    @Test
    void optionalStreamDropsEmptiesAndKeepsOrder() {
        assertThat(shapes.knownPrices(List.of("TEE-0002", "XXX-0000", "MUG-0001", "YYY-0000")))
                .containsExactly(20_00L, 12_50L);
    }

    @Test
    void optionalMapAndFlatMapHaveTheSameShape() {
        assertThat(shapes.price("MUG-0001").map(JdkResultShapes::withTax)).contains(15_00L);
        assertThat(shapes.price("XXX-0000").map(JdkResultShapes::withTax)).isEmpty();
        assertThat(shapes.bundleOf("MUG-0001").flatMap(shapes::price)).contains(10_00L);
        assertThat(shapes.bundleOf("TEE-0002").flatMap(shapes::price)).isEmpty();
    }

    @Test
    void streamFlatMapExpandsEachSkuIntoItsLines() {
        assertThat(shapes.bundleSkus(List.of("MUG-0001", "TEE-0002")))
                .containsExactly("MUG-0001", "COASTER-0003", "TEE-0002");
    }

    @Test
    void asynchronousTotalComposesAndRecovers() {
        assertThat(shapes.totalAsync("MUG-0001").join()).isEqualTo(15_00L + 4_99L);
        assertThat(shapes.describeAsync("MUG-0001").join()).isEqualTo("MUG-0001: 19.99 incl. tax and shipping");
        assertThat(shapes.describeAsync("XXX-0000").join()).isEqualTo("XXX-0000: failed (unknown sku XXX-0000)");
        assertThat(shapes.totalAsync("XXX-0000")).isCompletedExceptionally();
        String kind = FutureResults.toResult(shapes.totalAsync("XXX-0000"))
                .fold(_ -> "ok", e -> e.getClass().getSimpleName());
        assertThat(kind).isEqualTo(NoSuchElementException.class.getSimpleName());
    }

    @Test
    void anUnknownPriceIsEmpty() {
        assertThat(shapes.price("XXX-0000")).isEqualTo(Optional.empty());
    }

    @Test
    void demoPrintsTheSameShapeAcrossTheJdk() {
        assertThat(Console.capture(() -> JdkResultShapesDemo.main(new String[0]))).isEqualTo("""
                -- Optional: map / flatMap
                price(MUG-0001).map(withTax)          = Optional[1500]
                price(XXX-0000).map(withTax)          = Optional.empty
                bundleOf(MUG-0001).flatMap(price)     = Optional[1000]
                -- Stream: map / flatMap
                bundleSkus([MUG-0001, TEE-0002])      = [MUG-0001, COASTER-0003, TEE-0002]
                knownPrices(TEE, XXX, MUG)            = [2000, 1250]
                -- CompletableFuture: thenApply / thenCompose / handle
                completedFuture(2).thenApply(x10).thenCompose(+1) = 21
                describeAsync(MUG-0001) = MUG-0001: 19.99 incl. tax and shipping
                describeAsync(XXX-0000) = XXX-0000: failed (unknown sku XXX-0000)
                -- CompletableFuture -> Result
                toResult(totalAsync(MUG-0001)) = Ok[value=1999]
                toResult(totalAsync(XXX-0000)) = Err[error=java.util.NoSuchElementException: unknown sku XXX-0000]
                """);
    }
}
