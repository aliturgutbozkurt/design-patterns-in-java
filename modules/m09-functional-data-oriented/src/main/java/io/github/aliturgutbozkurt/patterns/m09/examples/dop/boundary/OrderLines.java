package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import java.util.List;

/**
 * The core: works on valid values only, so it contains no validation code at all.
 *
 * @see "m09 lesson, section Data-oriented programming — parse, don't validate"
 */
public final class OrderLines {

    private OrderLines() {}

    public static long totalCents(List<OrderLine> lines) {
        return lines.stream().mapToLong(line -> line.quantity().value() * line.unitPriceCents()).sum();
    }
}
