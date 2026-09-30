package io.github.aliturgutbozkurt.patterns.m01.examples.srp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InMemoryInvoiceRepository;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.Invoice;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceCalculator;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceFormatter;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceLine;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceMailer;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceTotals;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.after.InvoiceWorkflow;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.before.InvoiceService;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    static final String REFERENCE_TEXT = """
            INVOICE INV-001
            Customer: Ada Lovelace
              2 x Keyboard         49.90     99.80
              1 x Mouse            19.99     19.99
              1 x Monitor         229.00    229.00
            Subtotal:    348.79
            VAT 20%:      69.76
            Total:       418.55
            """;

    static Invoice sampleInvoice() {
        return new Invoice("INV-001", "Ada Lovelace", List.of(
                new InvoiceLine("Keyboard", 2, new BigDecimal("49.90")),
                new InvoiceLine("Mouse", 1, new BigDecimal("19.99")),
                new InvoiceLine("Monitor", 1, new BigDecimal("229.00"))));
    }

    static List<InvoiceService.Line> sampleLines() {
        return List.of(
                new InvoiceService.Line("Keyboard", 2, new BigDecimal("49.90")),
                new InvoiceService.Line("Mouse", 1, new BigDecimal("19.99")),
                new InvoiceService.Line("Monitor", 1, new BigDecimal("229.00")));
    }

    @Test
    void godClassProducesTheReferenceInvoice() {
        var service = new InvoiceService();
        String printed = Console.capture(() -> assertThat(service.process("INV-001", "Ada Lovelace", sampleLines()))
                .isEqualTo(REFERENCE_TEXT));
        assertThat(printed).isEqualTo("Emailing invoice INV-001 to Ada Lovelace\n");
        assertThat(service.find("INV-001")).contains(REFERENCE_TEXT);
    }

    @Test
    void refactoredWorkflowBehavesExactlyLikeTheGodClass() {
        var before = Console.capture(() -> System.out.print(new InvoiceService().process("INV-001", "Ada Lovelace", sampleLines())));
        var workflow = new InvoiceWorkflow(new InvoiceCalculator(), new InvoiceFormatter(),
                new InMemoryInvoiceRepository(), new InvoiceMailer());
        var after = Console.capture(() -> System.out.print(workflow.process(sampleInvoice())));
        assertThat(after).isEqualTo(before);
    }

    @Test
    void calculatorAddsTwentyPercentVatRoundedHalfEven() {
        assertThat(new InvoiceCalculator().totals(sampleInvoice())).isEqualTo(new InvoiceTotals(
                new BigDecimal("348.79"), new BigDecimal("69.76"), new BigDecimal("418.55")));
    }

    @Test
    void formatterNeedsNoCalculatorOrStorage() {
        var invoice = new Invoice("X-9", "Bob", List.of(new InvoiceLine("Pen", 3, new BigDecimal("1.00"))));
        var totals = new InvoiceTotals(new BigDecimal("3.00"), new BigDecimal("0.60"), new BigDecimal("3.60"));
        assertThat(new InvoiceFormatter().format(invoice, totals)).isEqualTo("""
                INVOICE X-9
                Customer: Bob
                  3 x Pen               1.00      3.00
                Subtotal:      3.00
                VAT 20%:       0.60
                Total:         3.60
                """);
    }

    @Test
    void repositoryStoresAndFindsByNumber() {
        var repository = new InMemoryInvoiceRepository();
        repository.save(sampleInvoice());
        assertThat(repository.findByNumber("INV-001")).contains(sampleInvoice());
        assertThat(repository.findByNumber("nope")).isEmpty();
    }

    @Test
    void workflowSavesTheInvoice() {
        var repository = new InMemoryInvoiceRepository();
        var workflow = new InvoiceWorkflow(new InvoiceCalculator(), new InvoiceFormatter(), repository, new InvoiceMailer());
        Console.capture(() -> workflow.process(sampleInvoice()));
        assertThat(repository.findByNumber("INV-001")).isPresent();
    }

    @Test
    void invoiceRejectsInvalidData() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Invoice("INV-1", "Ada", List.of()));
        assertThatIllegalArgumentException().isThrownBy(() -> new InvoiceLine("Pen", 0, BigDecimal.ONE));
        assertThatIllegalArgumentException().isThrownBy(() -> new InvoiceLine("Pen", 1, new BigDecimal("-1")));
    }

    @Test
    void demoPrintsBothVersions() {
        assertThat(Console.capture(() -> SrpDemo.main(new String[0]))).isEqualTo(
                "== before: one class, four reasons to change ==\n"
                        + "Emailing invoice INV-001 to Ada Lovelace\n"
                        + REFERENCE_TEXT
                        + "== after: four collaborators, one coordinator ==\n"
                        + "Emailing invoice INV-001 to Ada Lovelace\n"
                        + REFERENCE_TEXT
                        + "same text? true\n");
    }
}
