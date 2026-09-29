package io.github.aliturgutbozkurt.patterns.m06.examples.iterator.spliterator;

import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A spliterator over a paged source: {@code tryAdvance} hands out one customer and fetches the next page only when the
 * current one is used up. The size is unknown and it does not split (the pages must be read in order).
 *
 * @see "m06 lesson, section Iterator"
 */
public final class PagedSpliterator implements Spliterator<String> {

    private final PagedCustomerSource source;
    private List<String> page = List.of();
    private int index;
    private int nextPage;
    private boolean lastPageSeen;

    public PagedSpliterator(PagedCustomerSource source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    public boolean tryAdvance(Consumer<? super String> action) {
        while (index >= page.size()) {
            if (lastPageSeen) {
                return false;
            }
            page = source.fetchPage(nextPage++);
            index = 0;
            lastPageSeen = page.size() < source.pageSize();
        }
        action.accept(page.get(index++));
        return true;
    }

    @Override
    public Spliterator<String> trySplit() {
        return null;  // cannot split: page n + 1 is only known after page n
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;  // unknown
    }

    @Override
    public int characteristics() {
        return ORDERED | NONNULL;
    }
}
