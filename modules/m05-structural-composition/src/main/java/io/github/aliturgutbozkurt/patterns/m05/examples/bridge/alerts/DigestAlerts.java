package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts;

import java.util.ArrayList;
import java.util.List;

/**
 * Refined abstraction: alerts are collected and {@link #flush()} sends them as one combined message, in order.
 *
 * @see "m05 lesson, section Bridge — modern Java 27"
 */
public final class DigestAlerts extends AlertService {

    private final List<String> pending = new ArrayList<>();

    public DigestAlerts(MessageChannel channel) {
        super(channel);
    }

    @Override
    public void raise(String alert) {
        pending.add(checked(alert));
    }

    public int pending() {
        return pending.size();
    }

    /** Sends one message such as {@code "2 alerts: cpu 85%; disk 80%"}; sends nothing if nothing is pending. */
    public void flush() {
        if (pending.isEmpty()) {
            return;
        }
        String count = pending.size() == 1 ? "1 alert" : pending.size() + " alerts";
        channel.send(count + ": " + String.join("; ", pending));
        pending.clear();
    }
}
