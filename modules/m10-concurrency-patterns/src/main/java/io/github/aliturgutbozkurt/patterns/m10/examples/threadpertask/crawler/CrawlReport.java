package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler;

import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The result of a crawl: the pages fetched successfully and the URLs that failed with their reason. Both are sorted
 * copies, so the report does not depend on the order in which concurrent fetches finished.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public record CrawlReport(SortedSet<String> visited, SortedMap<String, String> failures) {

    public CrawlReport {
        visited = Collections.unmodifiableSortedSet(new TreeSet<>(visited));
        failures = Collections.unmodifiableSortedMap(new TreeMap<>(failures));
    }
}
