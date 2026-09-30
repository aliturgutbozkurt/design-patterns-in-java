package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyEmployee;
import java.util.List;

/** GIVEN — do not modify. The operations the payroll offers; every one is a function over immutable data. */
public interface Payroll {

    /** Converts a legacy object into the matching record (the boundary). */
    Employee fromLegacy(LegacyEmployee legacy);

    long monthlyPayCents(Employee employee);

    String benefits(Employee employee);

    Kind kindOf(Employee employee);

    /** A new record with the pay raised by {@code percent} (0..100); the original is unchanged. */
    Employee withRaise(Employee employee, int percent);

    PayrollSummary summarize(List<Employee> employees);
}
