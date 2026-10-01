package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.List;

/**
 * Command: book a parcel for the physical items.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class BookShipping implements PostAction {

    private final Order order;
    private final int grams;

    public BookShipping(Order order, int grams) {
        this.order = order;
        this.grams = grams;
    }

    @Override
    public void execute(List<String> log) {
        log.add("book shipping " + order.getId() + " (" + grams + " g)");
    }
}
