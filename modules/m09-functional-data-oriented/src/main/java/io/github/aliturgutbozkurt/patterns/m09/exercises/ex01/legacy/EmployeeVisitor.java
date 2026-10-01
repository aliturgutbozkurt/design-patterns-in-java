package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. One method per legacy employee class (double dispatch). */
public interface EmployeeVisitor<R> {

    R visitSalaried(LegacySalaried employee);

    R visitHourly(LegacyHourly employee);

    R visitContractor(LegacyContractor employee);

    R visitIntern(LegacyIntern employee);
}
