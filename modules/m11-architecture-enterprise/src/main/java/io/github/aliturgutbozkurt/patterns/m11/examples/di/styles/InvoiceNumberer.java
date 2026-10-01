package io.github.aliturgutbozkurt.patterns.m11.examples.di.styles;

import java.time.LocalDate;

/**
 * Hands out invoice numbers such as {@code INV-2026-09-0001}; implemented three times to compare injection styles.
 *
 * @see "m11 lesson, section Dependency Injection — injection styles"
 */
public interface InvoiceNumberer {

    /** The next invoice number. */
    String next();

    /** The shared number format: year, month and a four-digit sequence. */
    static String format(LocalDate day, long sequence) {
        return "INV-%04d-%02d-%04d".formatted(day.getYear(), day.getMonthValue(), sequence);
    }
}
