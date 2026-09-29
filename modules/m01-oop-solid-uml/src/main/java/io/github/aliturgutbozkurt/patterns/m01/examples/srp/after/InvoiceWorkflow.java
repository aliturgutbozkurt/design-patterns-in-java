package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.util.Objects;

/**
 * Coordinates the four collaborators and does nothing else — its only reason to change is the order of the steps.
 *
 * @see "m01 lesson, section SRP"
 */
public final class InvoiceWorkflow {

    private final InvoiceCalculator calculator;
    private final InvoiceFormatter formatter;
    private final InvoiceRepository repository;
    private final InvoiceMailer mailer;

    public InvoiceWorkflow(InvoiceCalculator calculator, InvoiceFormatter formatter,
                           InvoiceRepository repository, InvoiceMailer mailer) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.mailer = Objects.requireNonNull(mailer, "mailer");
    }

    /** Calculates, formats, stores and sends the invoice; returns its text. */
    public String process(Invoice invoice) {
        String text = formatter.format(invoice, calculator.totals(invoice));
        repository.save(invoice);
        mailer.send(invoice, text);
        return text;
    }
}
