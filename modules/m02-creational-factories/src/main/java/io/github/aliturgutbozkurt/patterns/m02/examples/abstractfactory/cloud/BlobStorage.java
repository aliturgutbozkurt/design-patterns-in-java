package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

import java.util.Optional;

/**
 * Abstract product 1: object storage.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public interface BlobStorage {

    String provider();

    /** Stores {@code content} under {@code key} and returns its URI. */
    String put(String key, String content);

    Optional<String> get(String uri);
}
