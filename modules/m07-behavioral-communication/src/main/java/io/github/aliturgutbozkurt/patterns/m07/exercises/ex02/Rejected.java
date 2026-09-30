package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. {@code approver} rejected the expense for {@code reason}. */
public record Rejected(String approver, String reason) implements Decision {

    public Rejected {
        Objects.requireNonNull(approver, "approver");
        Objects.requireNonNull(reason, "reason");
    }
}
