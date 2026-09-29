package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Optional;

/** Assignment 02 — first link: company policy. */
public class PolicyCheck implements Approver {

    @Override
    public String name() {
        return "policy check";
    }

    @Override
    public Optional<Decision> review(Expense expense) {
        // TODO(ex02): reject non-positive amounts and MEALS above 100.00; otherwise pass it on (empty).
        throw new UnsupportedOperationException("TODO(ex02): implement PolicyCheck.review");
    }
}
