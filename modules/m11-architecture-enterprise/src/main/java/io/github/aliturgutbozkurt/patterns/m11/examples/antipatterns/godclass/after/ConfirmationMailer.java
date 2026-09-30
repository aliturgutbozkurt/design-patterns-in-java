package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * E-mail text and sending, extracted from the god class — the same text, byte for byte.
 *
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public final class ConfirmationMailer {

    private final List<String> sent = new ArrayList<>();

    public void sendConfirmation(PlacedOrder order) {
        sent.add("To: " + order.customer() + " | Your order " + order.id() + " (" + order.quantity()
                + " items) is confirmed. Total: " + BigDecimal.valueOf(order.totalCents(), 2).toPlainString());
    }

    /** Every e-mail sent, oldest first (an unmodifiable copy). */
    public List<String> sent() {
        return List.copyOf(sent);
    }
}
