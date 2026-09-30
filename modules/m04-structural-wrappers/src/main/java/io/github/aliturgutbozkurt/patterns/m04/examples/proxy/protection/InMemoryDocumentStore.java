package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Real subject: keeps documents in a map and checks no permissions at all.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class InMemoryDocumentStore implements DocumentStore {

    private final Map<String, String> documents = new HashMap<>();

    @Override
    public String read(String id) {
        String content = documents.get(id);
        if (content == null) {
            throw new NoSuchElementException("no document: " + id);
        }
        return content;
    }

    @Override
    public void write(String id, String content) {
        documents.put(id, content);
    }

    @Override
    public void delete(String id) {
        documents.remove(id);
    }
}
