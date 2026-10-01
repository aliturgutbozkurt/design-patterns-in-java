package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern;

/**
 * Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/features/shop/modern/InvoiceRunDemo.java}
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public final class InvoiceRunDemo {

    private InvoiceRunDemo() {}

    public static void main(String[] args) {
        System.out.print(InvoiceRun.run(InvoiceRun.sampleOrders()));
        System.out.println("-- modern package: " + InvoiceRun.TYPES.size() + " top-level types (classic: "
                + io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic.InvoiceRun.TYPES.size()
                + ")");
    }
}
