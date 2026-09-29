package io.github.aliturgutbozkurt.patterns.m05.examples.composite.orgchart;

/** Validation and formatting shared by every kind of {@link Employee} (package-private helper). */
final class Staff {

    private Staff() {}

    static void validate(String name, int salary) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("salary must not be negative: " + salary);
        }
    }

    static String line(int indent, String name, String role, int salary) {
        return "  ".repeat(indent) + name + " (" + role + ") " + salary + "\n";
    }
}
