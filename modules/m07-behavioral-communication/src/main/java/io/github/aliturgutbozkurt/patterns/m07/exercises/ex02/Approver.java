package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Optional;

/** GIVEN — do not modify. One link of the approval chain. */
public interface Approver {

    /** The name that appears in the trail, e.g. {@code "team lead"}. */
    String name();

    /** A decision, or {@code Optional.empty()} to pass the expense on to the next approver. */
    Optional<Decision> review(Expense expense);
}
