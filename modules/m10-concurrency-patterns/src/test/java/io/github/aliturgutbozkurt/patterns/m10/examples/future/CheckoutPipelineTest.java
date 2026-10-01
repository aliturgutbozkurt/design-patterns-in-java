package io.github.aliturgutbozkurt.patterns.m10.examples.future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.Cart;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.CheckoutPipeline;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.InMemoryShop;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.Receipt;
import io.github.aliturgutbozkurt.patterns.m10.support.Demos;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(10)
class CheckoutPipelineTest {

    private static final Executor CALLER_RUNS = Runnable::run;

    private static InMemoryShop shop(Executor executor) {
        return new InMemoryShop(executor, Map.of("book", 20_00L, "mug", 12_50L, "pen", 2_50L),
                Set.of("book", "pen"), 100_00L);
    }

    private static CheckoutPipeline pipeline(InMemoryShop shop, Executor executor) {
        return new CheckoutPipeline(executor, shop, shop, shop);
    }

    @Test
    void happyPathGivesAnApprovedReceipt() {
        var shop = shop(CALLER_RUNS);
        var receipt = pipeline(shop, CALLER_RUNS).checkout(new Cart("alice", List.of("book", "pen"))).join();
        assertThat(receipt).isEqualTo(new Receipt("alice", Receipt.Status.APPROVED, 22_50, "payment pay-1"));
    }

    @Test
    void withTheCallerRunsExecutorTheFutureIsAlreadyDoneWhenCheckoutReturns() {
        var shop = shop(CALLER_RUNS);
        var future = pipeline(shop, CALLER_RUNS).checkout(new Cart("alice", List.of("book")));
        assertThat(future).isDone();
    }

    @Test
    void stockFailureGivesADeclinedReceiptWithTheUnwrappedCause() {
        var shop = shop(CALLER_RUNS);
        var receipt = pipeline(shop, CALLER_RUNS).checkout(new Cart("bob", List.of("book", "mug"))).join();
        assertThat(receipt).isEqualTo(new Receipt("bob", Receipt.Status.DECLINED, 0, "out of stock: mug"));
    }

    @Test
    void paymentIsNeverCalledWhenStockFails() {
        var shop = shop(CALLER_RUNS);
        pipeline(shop, CALLER_RUNS).checkout(new Cart("bob", List.of("mug"))).join();
        assertThat(shop.paymentCalls()).isZero();
    }

    @Test
    void paymentFailureAlsoBecomesADeclinedReceipt() {
        var shop = new InMemoryShop(CALLER_RUNS, Map.of("book", 20_00L), Set.of("book"), 30_00L);
        var receipt = pipeline(shop, CALLER_RUNS).checkout(new Cart("carol", List.of("book", "book"))).join();
        assertThat(receipt.status()).isEqualTo(Receipt.Status.DECLINED);
        assertThat(receipt.detail()).isEqualTo("card declined: 40.00 exceeds limit");
        assertThat(shop.paymentCalls()).isEqualTo(1);
    }

    @Test
    void joinOnAFailedStageThrowsCompletionExceptionAndGetThrowsExecutionException() {
        var failed = shop(CALLER_RUNS).reserve(new Cart("bob", List.of("mug")));
        assertThatThrownBy(failed::join).isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
        assertThatThrownBy(failed::get).isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    void theSamePipelineOnVirtualThreadsGivesTheSameReceipt() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var shop = shop(executor);
            var receipt = pipeline(shop, executor).checkout(new Cart("alice", List.of("book", "pen")))
                    .get(5, TimeUnit.SECONDS);
            assertThat(receipt).isEqualTo(new Receipt("alice", Receipt.Status.APPROVED, 22_50, "payment pay-1"));
        }
    }

    @Test
    void demoPrintsApprovedAndDeclinedReceipts() {
        assertThat(Demos.output(() -> CheckoutPipelineDemo.main(new String[0]))).isEqualTo("""
                alice: Receipt[customer=alice, status=APPROVED, totalCents=2250, detail=payment pay-1]
                bob:   Receipt[customer=bob, status=DECLINED, totalCents=0, detail=out of stock: mug]
                carol: Receipt[customer=carol, status=DECLINED, totalCents=0, detail=card declined: 120.00 exceeds limit]
                payment service called 2 times (never for bob)
                done when checkout() returned (Runnable::run): true
                same receipt for alice on virtual threads: true
                """);
    }
}
