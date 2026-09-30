package io.github.aliturgutbozkurt.patterns.m01.examples.dip.after;

/**
 * Another detail; adding it required no change to {@link NotificationService}.
 *
 * @see "m01 lesson, section DIP"
 */
public final class SmsSender implements MessageSender {

    @Override
    public void send(String to, String message) {
        System.out.println("SMS to " + to + ": " + message);
    }
}
