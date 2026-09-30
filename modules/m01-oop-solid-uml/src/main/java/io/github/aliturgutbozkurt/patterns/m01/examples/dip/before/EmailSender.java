package io.github.aliturgutbozkurt.patterns.m01.examples.dip.before;

/**
 * A low-level detail: pretends to talk to an SMTP server by printing.
 *
 * @see "m01 lesson, section DIP"
 */
public final class EmailSender {

    public void send(String to, String message) {
        System.out.println("EMAIL to " + to + ": " + message);
    }
}
