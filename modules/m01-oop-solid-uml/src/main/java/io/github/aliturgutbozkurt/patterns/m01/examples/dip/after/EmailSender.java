package io.github.aliturgutbozkurt.patterns.m01.examples.dip.after;

/**
 * A detail that implements the high-level abstraction.
 *
 * @see "m01 lesson, section DIP"
 */
public final class EmailSender implements MessageSender {

    @Override
    public void send(String to, String message) {
        System.out.println("EMAIL to " + to + ": " + message);
    }
}
