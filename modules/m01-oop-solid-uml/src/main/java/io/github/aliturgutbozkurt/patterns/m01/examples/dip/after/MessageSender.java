package io.github.aliturgutbozkurt.patterns.m01.examples.dip.after;

/**
 * The abstraction is owned by the high-level package (next to {@link NotificationService}); the low-level senders
 * depend on it — the dependency arrow is inverted.
 *
 * @see "m01 lesson, section DIP"
 */
@FunctionalInterface
public interface MessageSender {

    void send(String to, String message);
}
