package io.github.aliturgutbozkurt.patterns.m11.examples.archcheck;

import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.adapter.InvoiceDao;
import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.domain.Invoice;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderLine;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.List;
import java.util.Set;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/archcheck/ArchCheckDemo.java}
 *
 * @see "m11 lesson, section Architecture rules — how the tools work"
 */
public final class ArchCheckDemo {

    private static final String ROOT = "io.github.aliturgutbozkurt.patterns.m11.examples.";

    private ArchCheckDemo() {}

    public static void main(String[] args) {
        var scanner = new DependencyScanner();
        List<Class<?>> classes = List.of(Invoice.class, InvoiceDao.class, Order.class, OrderEvent.class,
                OrderEvent.OrderPlaced.class, OrderLine.class, OrderId.class, Money.class, Sku.class);

        report("erosion.domain", Set.of("erosion.adapter"), scanner, classes);
        report("hexagonal.shop.domain", Set.of("hexagonal.shop.application", "hexagonal.shop.adapter",
                "hexagonal.shop.config"), scanner, classes);
        System.out.println("class-file major version: " + scanner.majorVersion(Invoice.class));
    }

    private static void report(String layer, Set<String> forbidden, DependencyScanner scanner, List<Class<?>> classes) {
        Set<String> prefixes = Set.copyOf(forbidden.stream().map(p -> ROOT + p).toList());
        List<Violation> violations = new LayerRule(ROOT + layer, prefixes).check(scanner, classes);
        System.out.println(layer + ": " + violations.size() + " violation(s)");
        violations.forEach(v -> System.out.println("  " + v.toString().replace(ROOT, "")));
    }
}
