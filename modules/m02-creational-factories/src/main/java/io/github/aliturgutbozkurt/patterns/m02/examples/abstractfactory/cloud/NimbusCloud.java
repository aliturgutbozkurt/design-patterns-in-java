package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

/**
 * The fictional "Nimbus" provider family (in-memory simulation, region tr-central).
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class NimbusCloud implements CloudFactory {

    @Override
    public String name() {
        return "Nimbus";
    }

    @Override
    public BlobStorage storage() {
        return new InMemoryStorage(name(), "nimbus", "tr-central");
    }

    @Override
    public MessageQueue queue() {
        return new InMemoryQueue(name());
    }
}
