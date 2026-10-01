package io.github.aliturgutbozkurt.patterns.m11.examples.repository.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order aggregate with a version number. The version is the one the order was <em>loaded</em> at; the repository
 * compares it with the stored version on save (optimistic concurrency) and bumps it.
 *
 * @see "m11 lesson, section Repository — optimistic versioning"
 */
public final class Order {

    private final String id;
    private final List<OrderLine> lines;
    private long version;

    /** A new order that has never been saved (version 0). */
    public Order(String id) {
        this(id, List.of(), 0);
    }

    Order(String id, List<OrderLine> lines, long version) {
        this.id = Objects.requireNonNull(id, "id");
        this.lines = new ArrayList<>(lines);
        this.version = version;
    }

    public String id() {
        return id;
    }

    /** The version this copy was loaded at (0 = never saved). */
    public long version() {
        return version;
    }

    /** An unmodifiable copy of the lines. */
    public List<OrderLine> lines() {
        return List.copyOf(lines);
    }

    public void addLine(String sku, int quantity) {
        lines.add(new OrderLine(sku, quantity));
    }

    /** An independent copy: what the repository stores and hands out. */
    Order copy() {
        return new Order(id, lines, version);
    }

    /** Called by the repository after a successful save. */
    void savedAt(long newVersion) {
        version = newVersion;
    }
}
