package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler;

import java.io.IOException;

/**
 * A blocking HTTP client, reduced to one call. On a virtual thread, blocking here is cheap: the thread is parked and
 * its carrier runs other work.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
@FunctionalInterface
public interface WebClient {

    /** Fetches {@code url}; blocks until the page arrives. */
    Page fetch(String url) throws IOException;
}
