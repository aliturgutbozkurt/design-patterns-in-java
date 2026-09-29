package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

/**
 * Abstract Factory for a cloud provider: storage and queue always come from the same provider, so URIs, regions and
 * credentials never get mixed.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public interface CloudFactory {

    String name();

    BlobStorage storage();

    MessageQueue queue();
}
