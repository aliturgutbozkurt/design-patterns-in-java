package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. The legacy monthly-pay rules; your code must give the same numbers. */
public class MonthlyPayVisitor implements EmployeeVisitor<Long> {

    public static final int REGULAR_HOURS = 160;

    @Override
    public Long visitSalaried(LegacySalaried e) {
        return e.getAnnualSalaryCents() / 12;
    }

    @Override
    public Long visitHourly(LegacyHourly e) {
        int regular = Math.min(e.getHoursThisMonth(), REGULAR_HOURS);
        int overtime = Math.max(0, e.getHoursThisMonth() - REGULAR_HOURS);
        return regular * e.getHourlyRateCents() + overtime * (e.getHourlyRateCents() * 3 / 2);
    }

    @Override
    public Long visitContractor(LegacyContractor e) {
        return e.isVatRegistered() ? e.getInvoiceCents() * 120 / 100 : e.getInvoiceCents();
    }

    @Override
    public Long visitIntern(LegacyIntern e) {
        return e.isUniversityFunded() ? 0L : e.getStipendCents();
    }
}
