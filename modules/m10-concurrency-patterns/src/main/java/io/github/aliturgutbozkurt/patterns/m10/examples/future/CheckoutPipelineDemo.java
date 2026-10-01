package io.github.aliturgutbozkurt.patterns.m10.examples.future;

import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.Cart;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.CheckoutPipeline;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.InMemoryShop;
import io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout.Receipt;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Run: {@code java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/future/CheckoutPipelineDemo.java}
 *
 * <p>With {@code Runnable::run} the whole pipeline runs synchronously inside {@code checkout}; the same code on
 * virtual threads gives the same receipt.
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public final class CheckoutPipelineDemo {

    private static final Map<String, Long> PRICES = Map.of("book", 20_00L, "mug", 12_50L, "pen", 2_50L);
    private static final Set<String> IN_STOCK = Set.of("book", "pen");

    private CheckoutPipelineDemo() {}

    public static void main(String[] args) throws Exception {
        Executor callerRuns = Runnable::run;
        var shop = new InMemoryShop(callerRuns, PRICES, IN_STOCK, 100_00L);
        var pipeline = new CheckoutPipeline(callerRuns, shop, shop, shop);

        var alice = pipeline.checkout(new Cart("alice", List.of("book", "pen")));
        System.out.println("alice: " + alice.join());
        System.out.println("bob:   " + pipeline.checkout(new Cart("bob", List.of("book", "mug"))).join());
        System.out.println("carol: " + pipeline.checkout(new Cart("carol", List.of("book", "book", "book", "book",
                "book", "book"))).join());
        System.out.println("payment service called " + shop.paymentCalls() + " times (never for bob)");
        System.out.println("done when checkout() returned (Runnable::run): " + alice.isDone());

        try (var virtualThreads = Executors.newVirtualThreadPerTaskExecutor()) {
            var asyncShop = new InMemoryShop(virtualThreads, PRICES, IN_STOCK, 100_00L);
            Receipt receipt = new CheckoutPipeline(virtualThreads, asyncShop, asyncShop, asyncShop)
                    .checkout(new Cart("alice", List.of("book", "pen"))).get();
            System.out.println("same receipt for alice on virtual threads: " + receipt.equals(alice.join()));
        }
    }
}
