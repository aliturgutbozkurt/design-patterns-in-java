package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

import java.util.List;

/**
 * Command: e-mail the invoice to the customer.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public class EmailInvoice implements PostAction {

    private final Order order;

    public EmailInvoice(Order order) {
        this.order = order;
    }

    @Override
    public void execute(List<String> log) {
        log.add("email invoice " + order.getId() + " to " + order.getCustomer());
    }
}
