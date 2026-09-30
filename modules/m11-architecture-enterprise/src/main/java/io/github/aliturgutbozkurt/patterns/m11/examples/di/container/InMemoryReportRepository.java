package io.github.aliturgutbozkurt.patterns.m11.examples.di.container;

import java.util.Collections;
import java.util.SequencedMap;
import java.util.TreeMap;

/**
 * Fixed sales figures; has one public no-argument constructor, so the container can create it.
 *
 * @see "m11 lesson, section Dependency Injection — how a container works"
 */
public final class InMemoryReportRepository implements ReportRepository {

    @Override
    public SequencedMap<String, Integer> unitsSold() {
        var units = new TreeMap<String, Integer>();
        units.put("pen", 5);
        units.put("book", 3);
        units.put("mug", 1);
        return Collections.unmodifiableSequencedMap(units);
    }
}
