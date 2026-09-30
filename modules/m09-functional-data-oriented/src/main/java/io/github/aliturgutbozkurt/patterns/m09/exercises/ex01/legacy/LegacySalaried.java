package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. A salaried employee; the salary is per year, in cents. */
public class LegacySalaried extends LegacyEmployee {

    private final long annualSalaryCents;

    public LegacySalaried(String id, String name, long annualSalaryCents) {
        super(id, name);
        this.annualSalaryCents = annualSalaryCents;
    }

    public long getAnnualSalaryCents() {
        return annualSalaryCents;
    }

    @Override
    public <R> R accept(EmployeeVisitor<R> visitor) {
        return visitor.visitSalaried(this);
    }
}
