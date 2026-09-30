package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Optional;

/** Assignment 02 — approves large expenses. */
public class Director implements Approver {

    @Override
    public String name() {
        return "director";
    }

    @Override
    public Optional<Decision> review(Expense expense) {
        // TODO(ex02): approve up to 20 000.00 (inclusive); otherwise pass it on.
        throw new UnsupportedOperationException("TODO(ex02): implement Director.review");
    }
}
