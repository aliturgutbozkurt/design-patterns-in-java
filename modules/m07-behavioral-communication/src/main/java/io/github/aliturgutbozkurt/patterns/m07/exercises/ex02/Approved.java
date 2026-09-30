package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. {@code approver} approved the expense. */
public record Approved(String approver) implements Decision {

    public Approved {
        Objects.requireNonNull(approver, "approver");
    }
}
