package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.List;
import java.util.Objects;

/** GIVEN — do not modify. The final decision and the names of every approver that reviewed the expense, in order. */
public record ApprovalResult(String expenseId, Decision decision, List<String> trail) {

    public ApprovalResult {
        Objects.requireNonNull(expenseId, "expenseId");
        Objects.requireNonNull(decision, "decision");
        trail = List.copyOf(trail);
    }
}
