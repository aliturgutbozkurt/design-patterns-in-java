package io.github.aliturgutbozkurt.patterns.capstone.api.sim;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.Notification;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.NotificationGateway;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * GIVEN — do not modify. Writes each notification as one line {@code NOTIFY <recipient> | <subject> | <body>} to a
 * sink (e.g. {@code System.out::println}).
 *
 * @see "capstone brief §2.2 — Events and notifications (F8)"
 */
public final class ConsoleNotifications implements NotificationGateway {

    private final Consumer<String> sink;

    public ConsoleNotifications(Consumer<String> sink) {
        this.sink = Objects.requireNonNull(sink, "sink");
    }

    @Override
    public void send(Notification notification) {
        sink.accept("NOTIFY " + notification.recipient() + " | " + notification.subject() + " | "
                + notification.body());
    }
}
