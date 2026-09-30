package io.github.aliturgutbozkurt.patterns.m11.examples.events.aggregate;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Handler: e-mails a confirmation when an order is placed. The {@link Order} does not know it exists.
 *
 * @see "m11 lesson, section Domain events"
 */
public final class SendConfirmation implements Consumer<OrderEvent.OrderPlaced> {

    private final Consumer<String> mailbox;

    public SendConfirmation(Consumer<String> mailbox) {
        this.mailbox = Objects.requireNonNull(mailbox, "mailbox");
    }

    @Override
    public void accept(OrderEvent.OrderPlaced placed) {
        mailbox.accept("mail: order " + placed.orderId() + " confirmed, total " + BigDecimal.valueOf(placed.totalCents(), 2));
    }
}
