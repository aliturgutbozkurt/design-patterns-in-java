package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Thread-per-task: every fetch is an ordinary blocking call running in its own task. With an executor such as
 * {@code Executors.newVirtualThreadPerTaskExecutor()} each task gets its own virtual thread, so the code reads like
 * sequential code and still keeps hundreds of requests in flight.
 *
 * <p>The crawl goes level by level (breadth first): all pages of one depth are fetched concurrently, and the links
 * they contain form the next level. A concurrent {@link Set} de-duplicates URLs across the concurrent fetches, so
 * every URL is fetched at most once and its depth is its shortest link distance from the start.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class Crawler {

    private final WebClient client;
    private final ExecutorService executor;
    private final int maxDepth;

    /** The executor is only used, never shut down: its owner decides its lifetime. */
    public Crawler(WebClient client, ExecutorService executor, int maxDepth) {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("maxDepth must not be negative: " + maxDepth);
        }
        this.client = Objects.requireNonNull(client, "client");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.maxDepth = maxDepth;
    }

    /** Crawls from {@code startUrl}, following links up to {@code maxDepth} hops away. */
    public CrawlReport crawl(String startUrl) throws InterruptedException {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Map<String, String> failures = new ConcurrentHashMap<>();
        seen.add(startUrl);
        List<String> level = List.of(startUrl);
        for (int depth = 0; !level.isEmpty(); depth++) {
            boolean followLinks = depth < maxDepth;
            Queue<String> nextLevel = new ConcurrentLinkedQueue<>();
            List<Callable<Void>> fetches = new ArrayList<>();
            for (String url : level) {
                fetches.add(() -> {                         // one task (one virtual thread) per URL
                    try {
                        Page page = client.fetch(url);      // blocking I/O, written as plain sequential code
                        visited.add(url);
                        if (followLinks) {
                            page.links().stream().filter(seen::add).forEach(nextLevel::add);
                        }
                    } catch (IOException e) {
                        failures.put(url, e.getMessage());
                    }
                    return null;
                });
            }
            for (Future<Void> fetch : executor.invokeAll(fetches)) {   // waits until the whole level is done
                rethrowUnexpected(fetch);
            }
            level = List.copyOf(nextLevel);
        }
        return new CrawlReport(new TreeSet<>(visited), new TreeMap<>(failures));
    }

    private static void rethrowUnexpected(Future<Void> fetch) throws InterruptedException {
        try {
            fetch.get();
        } catch (ExecutionException e) {
            throw new IllegalStateException("fetch task failed unexpectedly", e.getCause());
        }
    }
}
