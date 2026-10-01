package io.github.aliturgutbozkurt.patterns.m11.examples.erosion.domain;

import io.github.aliturgutbozkurt.patterns.m11.examples.erosion.adapter.InvoiceDao;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Deliberately eroded design: a domain class that saves itself through an adapter. It compiles and works — only an
 * architecture rule notices that the domain now depends on persistence (and that the two packages form a cycle).
 *
 * @see "m11 lesson, section Architecture rules"
 */
public final class Invoice {

    private final String number;
    private final BigDecimal amount;

    public Invoice(String number, BigDecimal amount) {
        this.number = Objects.requireNonNull(number, "number");
        this.amount = Objects.requireNonNull(amount, "amount");
    }

    public String number() {
        return number;
    }

    public BigDecimal amount() {
        return amount;
    }

    /** The shortcut that erodes the architecture: domain → adapter. */
    public String save() {
        return new InvoiceDao().store(this);
    }
}
