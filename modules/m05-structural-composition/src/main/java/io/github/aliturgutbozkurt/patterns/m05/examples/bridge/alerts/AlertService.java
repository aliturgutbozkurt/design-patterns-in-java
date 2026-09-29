package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts;

import java.util.Objects;

/**
 * Bridge abstraction: <em>when and what</em> to send is decided here, <em>where</em> it goes is the
 * {@link MessageChannel} it was composed with.
 *
 * @see "m05 lesson, section Bridge — modern Java 27"
 */
public abstract class AlertService {

    protected final MessageChannel channel;

    protected AlertService(MessageChannel channel) {
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    /** Reports one alert; the policy decides when the channel sees it. */
    public abstract void raise(String alert);

    protected static String checked(String alert) {
        if (alert == null || alert.isBlank()) {
            throw new IllegalArgumentException("alert must not be blank");
        }
        return alert;
    }
}
