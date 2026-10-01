package io.github.aliturgutbozkurt.patterns.m10.examples.immutable.order;

import java.util.ArrayList;
import java.util.List;

/**
 * The classic mutable JavaBean, kept as a warning. {@link #getLines()} hands out the internal list, so any caller
 * can change the order behind its back; making it safe needs defensive copies in every getter and setter, and
 * sharing it between threads would also need a lock around every access. Never shared across threads here.
 *
 * @see "m10 lesson, section Immutable Object"
 */
public final class MutableOrder {

    private String id;
    private List<OrderLine> lines = new ArrayList<>();

    public MutableOrder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** Returns the internal list itself: the bug this class exists to show. */
    public List<OrderLine> getLines() {
        return lines;
    }

    /** Stores the caller's list itself: the caller can still change it afterwards. */
    public void setLines(List<OrderLine> lines) {
        this.lines = lines;
    }

    public void addLine(OrderLine line) {
        lines.add(line);
    }

    public Money getTotal() {
        return lines.stream().map(OrderLine::total).reduce(Money::plus).orElseThrow();
    }
}
