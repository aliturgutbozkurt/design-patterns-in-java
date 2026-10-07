package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import io.github.aliturgutbozkurt.patterns.capstone.api.external.Notification;
import io.github.aliturgutbozkurt.patterns.capstone.api.external.NotificationGateway;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Spy {@link NotificationGateway}: keeps every notification sent, in order. Thread-safe. */
public final class RecordingNotifications implements NotificationGateway {

    private final List<Notification> sent = new CopyOnWriteArrayList<>();

    @Override
    public void send(Notification notification) {
        sent.add(notification);
    }

    /** Every notification so far. */
    public List<Notification> all() {
        return List.copyOf(sent);
    }

    /** The notifications to one recipient. */
    public List<Notification> to(String recipient) {
        return sent.stream().filter(n -> n.recipient().equals(recipient)).toList();
    }
}
