package io.github.aliturgutbozkurt.patterns.m11.examples.di.container;

import java.util.SequencedMap;

/**
 * Where the sales figures come from; an interface, so the container needs a binding for it.
 *
 * @see "m11 lesson, section Dependency Injection — how a container works"
 */
public interface ReportRepository {

    /** Units sold per product, in product-name order. */
    SequencedMap<String, Integer> unitsSold();
}
