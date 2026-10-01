package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01;

import java.util.Objects;

/**
 * GIVEN — do not modify. The data-oriented model: one record per kind of employee, each carrying only its own
 * fields. Compact constructors reject blank ids and names and negative amounts.
 */
public sealed interface Employee permits Employee.Salaried, Employee.Hourly, Employee.Contractor, Employee.Intern {

    String id();

    String name();

    record Salaried(String id, String name, long annualSalaryCents) implements Employee {
        public Salaried {
            Checks.person(id, name);
            Checks.amount(annualSalaryCents, "annualSalaryCents");
        }
    }

    record Hourly(String id, String name, long hourlyRateCents, int hoursThisMonth) implements Employee {
        public Hourly {
            Checks.person(id, name);
            Checks.amount(hourlyRateCents, "hourlyRateCents");
            Checks.amount(hoursThisMonth, "hoursThisMonth");
        }
    }

    record Contractor(String id, String name, long invoiceCents, boolean vatRegistered) implements Employee {
        public Contractor {
            Checks.person(id, name);
            Checks.amount(invoiceCents, "invoiceCents");
        }
    }

    record Intern(String id, String name, long stipendCents, boolean universityFunded) implements Employee {
        public Intern {
            Checks.person(id, name);
            Checks.amount(stipendCents, "stipendCents");
        }
    }

    /** Shared invariant checks of the records. */
    final class Checks {
        private Checks() {}

        static void person(String id, String name) {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(name, "name");
            if (id.isBlank() || name.isBlank()) {
                throw new IllegalArgumentException("id and name must not be blank");
            }
        }

        static void amount(long value, String field) {
            if (value < 0) {
                throw new IllegalArgumentException(field + " must not be negative: " + value);
            }
        }
    }
}
