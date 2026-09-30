package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

/**
 * The fictional "Acme" provider family (in-memory simulation, region eu-west).
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class AcmeCloud implements CloudFactory {

    @Override
    public String name() {
        return "Acme";
    }

    @Override
    public BlobStorage storage() {
        return new InMemoryStorage(name(), "acme", "eu-west");
    }

    @Override
    public MessageQueue queue() {
        return new InMemoryQueue(name());
    }
}
