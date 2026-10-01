package io.github.aliturgutbozkurt.patterns.m10.examples.future.checkout;

import java.util.List;
import java.util.Objects;

/**
 * What a customer wants to buy (one entry per item, so {@code ["book", "book"]} is two books).
 *
 * @see "m10 lesson, section CompletableFuture pipelines"
 */
public record Cart(String customer, List<String> skus) {

    public Cart {
        Objects.requireNonNull(customer, "customer");
        skus = List.copyOf(skus);
    }
}
