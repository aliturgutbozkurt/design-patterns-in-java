package io.github.aliturgutbozkurt.patterns.m10.examples.threadpertask.crawler;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory web site: a map from URL to the links on that page. Every fetch blocks for a fixed latency, like a
 * network call, and is counted per URL so tests can prove that no page was fetched twice. A URL that is not in the
 * map answers {@code 404 Not Found}.
 *
 * @see "m10 lesson, section Thread-per-task with virtual threads"
 */
public final class FakeWeb implements WebClient {

    /** A page the catalogue's home page still links to, but which no longer exists. */
    public static final String RETIRED_URL = "https://shop.example/p/retired";

    private final Map<String, List<String>> pages;
    private final Duration latency;
    private final Map<String, Integer> fetchCounts = new ConcurrentHashMap<>();

    public FakeWeb(Map<String, List<String>> pages, Duration latency) {
        this.pages = Map.copyOf(pages);
        this.latency = Objects.requireNonNull(latency, "latency");
    }

    @Override
    public Page fetch(String url) throws IOException {
        fetchCounts.merge(url, 1, Integer::sum);
        try {
            Thread.sleep(latency);                  // simulated network time: parks a virtual thread cheaply
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("interrupted while fetching " + url);
        }
        List<String> links = pages.get(url);
        if (links == null) {
            throw new IOException("404 Not Found: " + url);
        }
        return new Page(url, links);
    }

    /** How often each URL was fetched so far (an immutable snapshot). */
    public Map<String, Integer> fetchCounts() {
        return Map.copyOf(fetchCounts);
    }

    /** URL of product page {@code n}, e.g. {@code https://shop.example/p/007}. */
    public static String productUrl(int n) {
        return String.format(Locale.ROOT, "https://shop.example/p/%03d", n);
    }

    /**
     * A catalogue of {@code size} product pages shaped like a binary tree: page {@code n} links to pages
     * {@code 2n + 1} and {@code 2n + 2} (when they exist) and back to page 0, so the site has cycles. Page 0 also
     * links to {@link #RETIRED_URL}, which does not exist.
     */
    public static Map<String, List<String>> catalogue(int size) {
        Map<String, List<String>> site = new HashMap<>();
        for (int n = 0; n < size; n++) {
            List<String> links = new ArrayList<>();
            for (int child : new int[] {2 * n + 1, 2 * n + 2}) {
                if (child < size) {
                    links.add(productUrl(child));
                }
            }
            links.add(n == 0 ? RETIRED_URL : productUrl(0));
            site.put(productUrl(n), links);
        }
        return Map.copyOf(site);
    }
}
