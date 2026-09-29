package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

/**
 * Responsibility: delivery. Here it only prints; a real mailer would talk to an SMTP server (see DIP).
 *
 * @see "m01 lesson, section SRP"
 */
public final class InvoiceMailer {

    public void send(Invoice invoice, String text) {
        System.out.println("Emailing invoice " + invoice.number() + " to " + invoice.customer());
    }
}
