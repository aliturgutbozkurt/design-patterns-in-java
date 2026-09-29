package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

import java.util.ArrayList;
import java.util.List;

/** Simulated queue shared by the fictional providers. */
final class InMemoryQueue implements MessageQueue {

    private final String provider;
    private final List<String> messages = new ArrayList<>();

    InMemoryQueue(String provider) {
        this.provider = provider;
    }

    @Override
    public String provider() {
        return provider;
    }

    @Override
    public void publish(String message) {
        messages.add(message);
    }

    @Override
    public List<String> messages() {
        return List.copyOf(messages);
    }
}
