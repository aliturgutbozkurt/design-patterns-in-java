package io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Composite node: a manager has reports, and every operation adds the manager's own share to the sum over the
 * reports. "Safe" GoF variant — {@code add}/{@code remove} exist only here, not on {@link Employee}.
 *
 * @see "m05 lesson, section Composite"
 */
public final class Manager implements Employee {

    private final String name;
    private final int ownSalary;
    private final List<Employee> reports = new ArrayList<>();

    public Manager(String name, int ownSalary) {
        Staff.validate(name, ownSalary);
        this.name = name;
        this.ownSalary = ownSalary;
    }

    /** Adds a direct report; rejects anyone who would make this manager report to themselves. */
    public void add(Employee report) {
        Objects.requireNonNull(report, "report");
        if (report == this || report instanceof Manager manager && manager.manages(this)) {
            throw new IllegalArgumentException(
                    report.name() + " cannot report to " + name + ": that would create a cycle");
        }
        reports.add(report);
    }

    /** Removes a direct report; returns whether it was there. */
    public boolean remove(Employee report) {
        return reports.remove(report);
    }

    /** Direct reports, as a read-only view. */
    public List<Employee> reports() {
        return Collections.unmodifiableList(reports);
    }

    public int ownSalary() {
        return ownSalary;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int salary() {
        int total = ownSalary;
        for (Employee report : reports) {
            total += report.salary();
        }
        return total;
    }

    @Override
    public int headcount() {
        int total = 1;
        for (Employee report : reports) {
            total += report.headcount();
        }
        return total;
    }

    @Override
    public String print(int indent) {
        var chart = new StringBuilder(Staff.line(indent, name, "Manager", ownSalary));
        for (Employee report : reports) {
            chart.append(report.print(indent + 1));
        }
        return chart.toString();
    }

    private boolean manages(Employee someone) {
        for (Employee report : reports) {
            if (report == someone || report instanceof Manager manager && manager.manages(someone)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Manager[" + name + "]";
    }
}
