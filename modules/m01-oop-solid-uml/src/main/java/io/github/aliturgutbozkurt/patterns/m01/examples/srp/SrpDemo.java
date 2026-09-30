package io.github.aliturgutbozkurt.patterns.m01.examples.srp;

import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InMemoryInvoiceRepository;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.Invoice;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceCalculator;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceFormatter;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceLine;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceMailer;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceWorkflow;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.before.InvoiceService;
import java.math.BigDecimal;
import java.util.List;

/** Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/srp/SrpDemo.java} */
public final class SrpDemo {

    private SrpDemo() {}

    public static void main(String[] args) {
        System.out.println("== before: one class, four reasons to change ==");
        String before = new InvoiceService().process("INV-001", "Ada Lovelace", List.of(
                new InvoiceService.Line("Keyboard", 2, new BigDecimal("49.90")),
                new InvoiceService.Line("Mouse", 1, new BigDecimal("19.99")),
                new InvoiceService.Line("Monitor", 1, new BigDecimal("229.00"))));
        System.out.print(before);

        System.out.println("== after: four collaborators, one coordinator ==");
        var workflow = new InvoiceWorkflow(new InvoiceCalculator(), new InvoiceFormatter(),
                new InMemoryInvoiceRepository(), new InvoiceMailer());
        String after = workflow.process(new Invoice("INV-001", "Ada Lovelace", List.of(
                new InvoiceLine("Keyboard", 2, new BigDecimal("49.90")),
                new InvoiceLine("Mouse", 1, new BigDecimal("19.99")),
                new InvoiceLine("Monitor", 1, new BigDecimal("229.00")))));
        System.out.print(after);

        System.out.println("same text? " + before.equals(after));
    }
}
