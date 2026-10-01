package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. A contractor who sends one invoice per month. */
public class LegacyContractor extends LegacyEmployee {

    private final long invoiceCents;
    private final boolean vatRegistered;

    public LegacyContractor(String id, String name, long invoiceCents, boolean vatRegistered) {
        super(id, name);
        this.invoiceCents = invoiceCents;
        this.vatRegistered = vatRegistered;
    }

    public long getInvoiceCents() {
        return invoiceCents;
    }

    public boolean isVatRegistered() {
        return vatRegistered;
    }

    @Override
    public <R> R accept(EmployeeVisitor<R> visitor) {
        return visitor.visitContractor(this);
    }
}
