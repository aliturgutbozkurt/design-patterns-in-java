package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. The legacy benefits rules; your code must give the same text. */
public class BenefitsVisitor implements EmployeeVisitor<String> {

    @Override
    public String visitSalaried(LegacySalaried e) {
        return "health, pension";
    }

    @Override
    public String visitHourly(LegacyHourly e) {
        return e.getHoursThisMonth() >= 80 ? "health" : "none";
    }

    @Override
    public String visitContractor(LegacyContractor e) {
        return "none";
    }

    @Override
    public String visitIntern(LegacyIntern e) {
        return "mentoring";
    }
}
