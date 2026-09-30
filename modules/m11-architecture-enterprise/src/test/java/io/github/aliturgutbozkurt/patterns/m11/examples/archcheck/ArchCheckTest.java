package io.github.aliturgutbozkurt.patterns.m11.examples.archcheck;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.adapter.InvoiceDao;
import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.domain.Invoice;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderEvent;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderLine;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class ArchCheckTest {

    private static final String ROOT = "io.github.aliturgutbozkurt.patterns.m11.examples.";

    /** A type that appears only as the type of an unused field. */
    static final class FieldOnly {
        @SuppressWarnings("unused") // the field exists only so that its type shows up in the class file
        private Clock clock;
    }

    /** A type that appears only as a generic type argument. */
    static final class GenericOnly {
        @SuppressWarnings("unused") // the field exists only so that its generic signature shows up in the class file
        private Consumer<OrderEvent> handler;
    }

    private final DependencyScanner scanner = new DependencyScanner();

    @Test
    void findsTheErodedDependencyFromInvoiceToTheDao() {
        var rule = new LayerRule(ROOT + "erosion.domain", Set.of(ROOT + "erosion.adapter"));
        assertThat(rule.check(scanner, List.of(Invoice.class, InvoiceDao.class)))
                .containsExactly(new Violation(Invoice.class.getName(), InvoiceDao.class.getName()));
    }

    @Test
    void reportsNoViolationForTheShopDomain() {
        var rule = new LayerRule(ROOT + "hexagonal.shop.domain", Set.of(ROOT + "hexagonal.shop.application",
                ROOT + "hexagonal.shop.adapter", ROOT + "hexagonal.shop.config"));
        assertThat(rule.check(scanner, List.of(Order.class, OrderEvent.class, OrderEvent.OrderPlaced.class,
                OrderLine.class, OrderId.class, Money.class, Sku.class))).isEmpty();
    }

    @Test
    void findsADependencyThatAppearsOnlyAsAFieldType() {
        assertThat(scanner.dependenciesOf(FieldOnly.class)).contains("java.time.Clock");
    }

    @Test
    void missesATypeThatAppearsOnlyAsAGenericTypeArgument() {
        // Documented limitation: generic arguments live in the Signature attribute, which the scanner does not read.
        assertThat(scanner.dependenciesOf(GenericOnly.class))
                .contains("java.util.function.Consumer")
                .doesNotContain(OrderEvent.class.getName());
    }

    @Test
    void classFilesReportMajorVersion71() {
        assertThat(scanner.majorVersion(Invoice.class)).isEqualTo(71);
    }

    @Test
    void demoPrintsViolationsPerLayer() {
        assertThat(Console.capture(() -> ArchCheckDemo.main(new String[0]))).isEqualTo("""
                erosion.domain: 1 violation(s)
                  erosion.domain.Invoice -> erosion.adapter.InvoiceDao
                hexagonal.shop.domain: 0 violation(s)
                class-file major version: 71
                """);
    }
}
