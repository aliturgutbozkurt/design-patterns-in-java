package io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart;

/**
 * Composite leaf: an engineer has no reports.
 *
 * @see "m05 lesson, section Composite"
 */
public record Engineer(String name, int salary) implements Employee {

    public Engineer {
        Staff.validate(name, salary);
    }

    @Override
    public int headcount() {
        return 1;
    }

    @Override
    public String print(int indent) {
        return Staff.line(indent, name, "Engineer", salary);
    }
}
