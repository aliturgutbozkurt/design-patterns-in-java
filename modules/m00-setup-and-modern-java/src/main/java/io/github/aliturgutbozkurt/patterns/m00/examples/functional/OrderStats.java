package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import io.github.aliturgutbozkurt.patterns.m00.examples.records.Money;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

/** Small stream pipelines over order lines: behaviour is passed around as lambdas and method references. */
public final class OrderStats {

    private OrderStats() {}

    /** Revenue per category, categories sorted by name. Assumes one currency. */
    public static Map<String, Money> revenueByCategory(List<OrderLine> lines) {
        return lines.stream().collect(groupingBy(
                OrderLine::category,
                TreeMap::new,
                Collectors.mapping(OrderLine::total, reducing(null, (a, b) -> a == null ? b : a.plus(b)))));
    }

    /** The line with the highest quantity, if any. */
    public static Optional<OrderLine> bestSeller(List<OrderLine> lines) {
        return lines.stream().max(Comparator.comparingInt(OrderLine::quantity));
    }

    /** Fixed-size batches with a Stream Gatherer (JEP 485); the last batch may be smaller. */
    public static List<List<OrderLine>> batches(List<OrderLine> lines, int size) {
        return lines.stream().gather(Gatherers.windowFixed(size)).toList();
    }

    /** Sequenced collections (JEP 431): a reversed view without copying or index arithmetic. */
    public static List<String> newestFirst(List<OrderLine> lines) {
        return lines.reversed().stream().map(OrderLine::product).toList();
    }
}
