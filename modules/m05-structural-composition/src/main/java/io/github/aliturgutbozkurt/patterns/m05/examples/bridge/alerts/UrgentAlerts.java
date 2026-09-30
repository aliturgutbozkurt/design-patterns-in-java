package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts;

/**
 * Refined abstraction: every alert goes out at once, marked {@code [URGENT]}.
 *
 * @see "m05 lesson, section Bridge — modern Java 27"
 */
public final class UrgentAlerts extends AlertService {

    public UrgentAlerts(MessageChannel channel) {
        super(channel);
    }

    @Override
    public void raise(String alert) {
        channel.send("[URGENT] " + checked(alert));
    }
}
