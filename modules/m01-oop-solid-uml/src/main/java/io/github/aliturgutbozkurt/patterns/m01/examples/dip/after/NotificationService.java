package io.github.aliturgutbozkurt.patterns.m01.examples.dip.after;

import io.github.aliturgutbozkurt.patterns.m01.examples.dip.Customer;
import java.util.Objects;

/**
 * High-level policy that depends only on {@link MessageSender}; the concrete senders arrive through the constructor.
 *
 * @see "m01 lesson, section DIP"
 */
public final class NotificationService {

    private final MessageSender email;
    private final MessageSender sms;

    public NotificationService(MessageSender email, MessageSender sms) {
        this.email = Objects.requireNonNull(email, "email");
        this.sms = Objects.requireNonNull(sms, "sms");
    }

    public void notifyShipped(Customer customer, String orderId) {
        String message = "Order " + orderId + " has shipped, " + customer.name() + ".";
        switch (customer.preferred()) {
            case EMAIL -> email.send(customer.email(), message);
            case SMS -> sms.send(customer.phone(), message);
        }
    }
}
