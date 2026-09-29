package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.time.Clock;

/**
 * Assignment 02 — your service. It may depend only on the three constructor parameters: no {@code new} on a store or
 * notifier, and no {@code LocalDate.now()} without the clock.
 */
public class LibraryLoanService implements LoanService {

    public LibraryLoanService(LoanStore store, Notifier notifier, Clock clock) {
        // TODO(ex02): keep the three dependencies; reject null.
    }

    @Override
    public Loan borrow(Member member, Book book) {
        // TODO(ex02): at most 3 active loans; due date = today (from the clock) + 14 days.
        throw new UnsupportedOperationException("TODO(ex02): implement borrow(Member, Book)");
    }

    @Override
    public void giveBack(Loan loan) {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement giveBack(Loan)");
    }

    @Override
    public int remindOverdue() {
        // TODO(ex02): one message per loan whose due date is before today.
        throw new UnsupportedOperationException("TODO(ex02): implement remindOverdue()");
    }
}
