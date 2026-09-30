package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.List;

/**
 * Command: archive the invoice.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class ArchiveInvoice implements PostAction {

    private final Order order;

    public ArchiveInvoice(Order order) {
        this.order = order;
    }

    @Override
    public void execute(List<String> log) {
        log.add("archive invoice " + order.getId());
    }
}
