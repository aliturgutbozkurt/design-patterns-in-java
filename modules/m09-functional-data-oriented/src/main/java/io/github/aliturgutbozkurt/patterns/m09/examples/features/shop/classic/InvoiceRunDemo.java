package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.classic;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/features/shop/classic/InvoiceRunDemo.java} */
public final class InvoiceRunDemo {

    private InvoiceRunDemo() {}

    public static void main(String[] args) {
        System.out.print(InvoiceRun.run(InvoiceRun.sampleOrders()));
        System.out.println("-- classic package: " + InvoiceRun.TYPES.size() + " top-level types");
    }
}
