package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment;

import java.util.List;

/**
 * A customer order entering the warehouse (immutable: it is handed from thread to thread).
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public record Order(long id, List<String> items) {

    public Order {
        if (id < 1) {
            throw new IllegalArgumentException("order id must be positive: " + id);
        }
        items = List.copyOf(items);
    }
}
