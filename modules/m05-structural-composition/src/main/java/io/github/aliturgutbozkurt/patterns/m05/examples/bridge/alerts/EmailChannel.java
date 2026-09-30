package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Concrete implementor: addresses a message to an e-mail recipient and hands it to a transport (an in-memory fake
 * in tests, {@code System.out::println} in the demo).
 *
 * @see "m05 lesson, section Bridge — modern Java 27"
 */
public final class EmailChannel implements MessageChannel {

    private final String address;
    private final Consumer<String> transport;

    public EmailChannel(String address, Consumer<String> transport) {
        this.address = Objects.requireNonNull(address, "address");
        this.transport = Objects.requireNonNull(transport, "transport");
    }

    @Override
    public void send(String message) {
        transport.accept("email to " + address + ": " + message);
    }
}
