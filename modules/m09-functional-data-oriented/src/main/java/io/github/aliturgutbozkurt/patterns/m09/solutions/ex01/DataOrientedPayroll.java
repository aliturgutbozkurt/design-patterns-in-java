package io.github.aliturgutbozkurt.patterns.m09.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Contractor;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Hourly;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Intern;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Employee.Salaried;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Kind;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.Payroll;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.PayrollSummary;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyContractor;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyEmployee;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyHourly;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacyIntern;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy.LegacySalaried;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reference solution: the payroll as exhaustive {@code switch} functions over sealed records, with the legacy objects
 * converted once at the boundary. No visitor, no {@code accept}.
 *
 * @see "m09 lesson, section Data-oriented programming"
 */
public class DataOrientedPayroll implements Payroll {

    private static final int REGULAR_HOURS = 160;

    @Override
    public Employee fromLegacy(LegacyEmployee legacy) {
        Objects.requireNonNull(legacy, "legacy");
        // LegacyEmployee is not sealed, so this switch needs a default: one more reason to convert at the boundary.
        return switch (legacy) {
            case LegacySalaried s -> new Salaried(s.getId(), s.getName(), s.getAnnualSalaryCents());
            case LegacyHourly h -> new Hourly(h.getId(), h.getName(), h.getHourlyRateCents(), h.getHoursThisMonth());
            case LegacyContractor c -> new Contractor(c.getId(), c.getName(), c.getInvoiceCents(), c.isVatRegistered());
            case LegacyIntern i -> new Intern(i.getId(), i.getName(), i.getStipendCents(), i.isUniversityFunded());
            default -> throw new IllegalArgumentException("unknown legacy type: " + legacy.getClass().getName());
        };
    }

    @Override
    public long monthlyPayCents(Employee employee) {
        return switch (employee) {
            case Salaried(_, _, var annual) -> annual / 12;
            case Hourly(_, _, var rate, var hours) -> Math.min(hours, REGULAR_HOURS) * rate
                    + Math.max(0, hours - REGULAR_HOURS) * (rate * 3 / 2);
            case Contractor(_, _, var invoice, var vat) -> vat ? invoice * 120 / 100 : invoice;
            case Intern(_, _, var stipend, var funded) -> funded ? 0 : stipend;
        };
    }

    @Override
    public String benefits(Employee employee) {
        return switch (employee) {
            case Salaried _ -> "health, pension";
            case Hourly(_, _, _, var hours) -> hours >= 80 ? "health" : "none";
            case Contractor _ -> "none";
            case Intern _ -> "mentoring";
        };
    }

    @Override
    public Kind kindOf(Employee employee) {
        return switch (employee) {
            case Salaried _ -> Kind.SALARIED;
            case Hourly _ -> Kind.HOURLY;
            case Contractor _ -> Kind.CONTRACTOR;
            case Intern _ -> Kind.INTERN;
        };
    }

    @Override
    public Employee withRaise(Employee employee, int percent) {
        Objects.requireNonNull(employee, "employee");
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be 0..100: " + percent);
        }
        return switch (employee) {
            case Salaried(var id, var name, var annual) -> new Salaried(id, name, raise(annual, percent));
            case Hourly(var id, var name, var rate, var hours) -> new Hourly(id, name, raise(rate, percent), hours);
            case Contractor(var id, var name, var invoice, var vat) ->
                    new Contractor(id, name, raise(invoice, percent), vat);
            case Intern(var id, var name, var stipend, var funded) ->
                    new Intern(id, name, raise(stipend, percent), funded);
        };
    }

    @Override
    public PayrollSummary summarize(List<Employee> employees) {
        Objects.requireNonNull(employees, "employees");
        Map<Kind, Long> byKind = new EnumMap<>(Kind.class);
        Arrays.stream(Kind.values()).forEach(kind -> byKind.put(kind, 0L));
        long total = 0;
        long max = Long.MIN_VALUE;
        for (Employee employee : employees) {
            long pay = monthlyPayCents(employee);
            total += pay;
            byKind.merge(kindOf(employee), pay, Long::sum);
            max = Math.max(max, pay);
        }
        long highest = max;
        List<String> ids = employees.stream().filter(e -> monthlyPayCents(e) == highest).map(Employee::id).toList();
        return new PayrollSummary(total, Map.copyOf(byKind), ids);
    }

    private static long raise(long amount, int percent) {
        return amount * (100 + percent) / 100;
    }
}
