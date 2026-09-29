package io.github.aliturgutbozkurt.patterns.m01.examples.isp.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A client that depends on the smallest role it needs: {@link Printer}.
 *
 * @see "m01 lesson, section ISP"
 */
public final class PrintQueue {

    private final Printer printer;
    private final List<String> pending = new ArrayList<>();

    public PrintQueue(Printer printer) {
        this.printer = Objects.requireNonNull(printer, "printer");
    }

    public void submit(String document) {
        pending.add(Objects.requireNonNull(document, "document"));
    }

    /** Prints every pending document in order and empties the queue. */
    public List<String> printAll() {
        List<String> results = pending.stream().map(printer::print).toList();
        pending.clear();
        return results;
    }
}
