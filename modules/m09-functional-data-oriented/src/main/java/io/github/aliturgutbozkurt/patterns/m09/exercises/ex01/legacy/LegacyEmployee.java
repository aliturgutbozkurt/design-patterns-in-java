package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. The legacy model: an abstract class with getters and a Visitor hook. */
public abstract class LegacyEmployee {

    private final String id;
    private final String name;

    protected LegacyEmployee(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract <R> R accept(EmployeeVisitor<R> visitor);
}
