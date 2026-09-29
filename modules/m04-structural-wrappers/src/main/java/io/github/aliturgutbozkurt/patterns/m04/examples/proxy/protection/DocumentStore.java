package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

/**
 * Subject: stores documents by id.
 *
 * @see "m04 lesson, section Proxy"
 */
public interface DocumentStore {

    String read(String id);

    void write(String id, String content);

    void delete(String id);
}
