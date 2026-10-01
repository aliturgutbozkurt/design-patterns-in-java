package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler;

import java.util.List;
import java.util.Objects;

/**
 * A fetched page and the links it contains (immutable, so it can be shared between threads freely).
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public record Page(String url, List<String> links) {

    public Page {
        Objects.requireNonNull(url, "url");
        links = List.copyOf(links);
    }
}
