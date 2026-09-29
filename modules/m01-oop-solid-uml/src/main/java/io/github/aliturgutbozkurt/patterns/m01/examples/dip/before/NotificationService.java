package io.github.aliturgutbozkurt.patterns.m01.examples.dip.before;

import io.github.aliturgutbozkurt.patterns.m01.examples.dip.Customer;

/**
 * DIP violation: high-level policy ("tell the customer the order shipped") creates and depends on a low-level
 * detail. It cannot use SMS, and it cannot be tested without really "sending".
 *
 * @see "m01 lesson, section DIP"
 */
public final class NotificationService {

    public void notifyShipped(Customer customer, String orderId) {
        var sender = new EmailSender();  // hard-wired dependency on a concrete class
        sender.send(customer.email(), "Order " + orderId + " has shipped, " + customer.name() + ".");
    }
}
