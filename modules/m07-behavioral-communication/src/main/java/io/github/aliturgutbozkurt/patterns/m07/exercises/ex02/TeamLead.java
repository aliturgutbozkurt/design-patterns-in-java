package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Optional;

/** Assignment 02 — approves small expenses. */
public class TeamLead implements Approver {

    @Override
    public String name() {
        return "team lead";
    }

    @Override
    public Optional<Decision> review(Expense expense) {
        // TODO(ex02): approve up to 500.00 (inclusive) except EQUIPMENT; otherwise pass it on.
        throw new UnsupportedOperationException("TODO(ex02): implement TeamLead.review");
    }
}
