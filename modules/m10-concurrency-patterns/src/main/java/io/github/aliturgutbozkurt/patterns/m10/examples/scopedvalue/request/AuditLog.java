package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Deep in the call chain: records who did what for which request. It reads the context from
 * {@link RequestContext} instead of taking it as parameters through every layer.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class AuditLog {

    /**
     * One audit entry.
     *
     * @see "m10 lesson, section Scoped Values"
     */
    public record Entry(String requestId, String principal, String action) {}

    private final Queue<Entry> entries = new ConcurrentLinkedQueue<>();

    /** Records {@code action} for the current request and principal. */
    public void record(String action) {
        entries.add(new Entry(RequestContext.REQUEST_ID.orElse("no-request"),
                RequestContext.PRINCIPAL.orElse(Principal.ANONYMOUS).name(), action));
    }

    /** All entries sorted by request id; within one request they keep the order in which they were recorded. */
    public List<Entry> entries() {
        List<Entry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparing(Entry::requestId));   // stable sort
        return List.copyOf(sorted);
    }
}
