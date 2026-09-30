package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.spliterator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A fake paged API ("GET /customers?page=n"): it returns a fixed number of customers per page and counts how many
 * pages were fetched, so tests can check that the stream is lazy. Not thread-safe.
 *
 * @see "m06 lesson, section Iterator"
 */
public final class PagedCustomerSource {

    private final List<String> customers;
    private final int pageSize;
    private int pagesFetched;

    public PagedCustomerSource(List<String> customers, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be > 0: " + pageSize);
        }
        this.customers = List.copyOf(customers);
        this.pageSize = pageSize;
    }

    /** One "network call": page {@code page} (0-based), shorter than {@code pageSize} or empty at the end. */
    public List<String> fetchPage(int page) {
        pagesFetched++;
        int from = Math.min(page * pageSize, customers.size());
        int to = Math.min(from + pageSize, customers.size());
        return customers.subList(from, to);
    }

    public int pageSize() {
        return pageSize;
    }

    public int pagesFetched() {
        return pagesFetched;
    }

    /** All customers as a lazy stream: a page is fetched only when the stream needs its first element. */
    public Stream<String> stream() {
        return StreamSupport.stream(new PagedSpliterator(this), false);
    }
}
