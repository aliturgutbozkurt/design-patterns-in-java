package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyEmployee;
import java.util.List;

/**
 * Assignment 01 — the payroll as functions over sealed records. Use exhaustive {@code switch} expressions with record
 * patterns and no {@code default}; do not implement {@code EmployeeVisitor} and do not call {@code accept}.
 */
public class DataOrientedPayroll implements Payroll {

    @Override
    public Employee fromLegacy(LegacyEmployee legacy) {
        // TODO(ex01): map LegacySalaried/LegacyHourly/LegacyContractor/LegacyIntern to the matching record
        //             (type patterns on the legacy object; getters only, no accept).
        throw new UnsupportedOperationException("TODO(ex01): implement DataOrientedPayroll.fromLegacy");
    }

    @Override
    public long monthlyPayCents(Employee employee) {
        // TODO(ex01): salaried annual / 12; hourly rate x hours, hours above 160 at rate * 3 / 2;
        //             contractor invoice (+20 % when VAT-registered); intern stipend, or 0 when university-funded.
        throw new UnsupportedOperationException("TODO(ex01): implement DataOrientedPayroll.monthlyPayCents");
    }

    @Override
    public String benefits(Employee employee) {
        // TODO(ex01): "health, pension" / "health" (hours >= 80) or "none" / "none" / "mentoring".
        throw new UnsupportedOperationException("TODO(ex01): implement DataOrientedPayroll.benefits");
    }

    @Override
    public Kind kindOf(Employee employee) {
        // TODO(ex01): one Kind per record.
        throw new UnsupportedOperationException("TODO(ex01): implement DataOrientedPayroll.kindOf");
    }

    @Override
    public Employee withRaise(Employee employee, int percent) {
        // TODO(ex01): percent 0..100 (else IllegalArgumentException); return a NEW record with the money field
        //             multiplied by (100 + percent) / 100.
        throw new UnsupportedOperationException("TODO(ex01): implement DataOrientedPayroll.withRaise");
    }

    @Override
    public PayrollSummary summarize(List<Employee> employees) {
        // TODO(ex01): total, an unmodifiable total per Kind (every Kind, 0 when absent), ids of all highest paid.
        throw new UnsupportedOperationException("TODO(ex01): implement DataOrientedPayroll.summarize");
    }
}
