package io.github.aliturgutbozkurt.patterns.m01.examples.srp.after;

import java.util.Optional;

/**
 * Responsibility: storage. A database version would implement this interface; nothing else changes.
 *
 * @see "m01 lesson, section SRP"
 */
public interface InvoiceRepository {

    void save(Invoice invoice);

    Optional<Invoice> findByNumber(String number);
}
