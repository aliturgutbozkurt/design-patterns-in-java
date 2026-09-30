package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Concrete implementor: an SMS carries at most 160 characters, so longer text is cut and ends in {@code …}.
 *
 * @see "m05 lesson, section Bridge — modern Java 27"
 */
public final class SmsChannel implements MessageChannel {

    public static final int MAX_LENGTH = 160;

    private final String phone;
    private final Consumer<String> transport;

    public SmsChannel(String phone, Consumer<String> transport) {
        this.phone = Objects.requireNonNull(phone, "phone");
        this.transport = Objects.requireNonNull(transport, "transport");
    }

    @Override
    public void send(String message) {
        String text = message.length() <= MAX_LENGTH ? message : message.substring(0, MAX_LENGTH - 1) + "…";
        transport.accept("sms to " + phone + ": " + text);
    }
}
