package io.github.aliturgutbozkurt.patterns.m09.examples.result.jdk;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/result/jdk/JdkResultShapesDemo.java} */
public final class JdkResultShapesDemo {

    private JdkResultShapesDemo() {}

    public static void main(String[] args) {
        var shapes = JdkResultShapes.sample();

        System.out.println("-- Optional: map / flatMap");
        System.out.println("price(MUG-0001).map(withTax)          = "
                + shapes.price("MUG-0001").map(JdkResultShapes::withTax));
        System.out.println("price(XXX-0000).map(withTax)          = "
                + shapes.price("XXX-0000").map(JdkResultShapes::withTax));
        System.out.println("bundleOf(MUG-0001).flatMap(price)     = "
                + shapes.bundleOf("MUG-0001").flatMap(shapes::price));

        System.out.println("-- Stream: map / flatMap");
        System.out.println("bundleSkus([MUG-0001, TEE-0002])      = "
                + shapes.bundleSkus(List.of("MUG-0001", "TEE-0002")));
        System.out.println("knownPrices(TEE, XXX, MUG)            = "
                + shapes.knownPrices(List.of("TEE-0002", "XXX-0000", "MUG-0001")));

        System.out.println("-- CompletableFuture: thenApply / thenCompose / handle");
        int joined = CompletableFuture.completedFuture(2)
                .thenApply(x -> x * 10)
                .thenCompose(x -> CompletableFuture.completedFuture(x + 1))
                .join();
        System.out.println("completedFuture(2).thenApply(x10).thenCompose(+1) = " + joined);
        System.out.println("describeAsync(MUG-0001) = " + shapes.describeAsync("MUG-0001").join());
        System.out.println("describeAsync(XXX-0000) = " + shapes.describeAsync("XXX-0000").join());

        System.out.println("-- CompletableFuture -> Result");
        System.out.println("toResult(totalAsync(MUG-0001)) = " + FutureResults.toResult(shapes.totalAsync("MUG-0001")));
        System.out.println("toResult(totalAsync(XXX-0000)) = " + FutureResults.toResult(shapes.totalAsync("XXX-0000")));
    }
}
