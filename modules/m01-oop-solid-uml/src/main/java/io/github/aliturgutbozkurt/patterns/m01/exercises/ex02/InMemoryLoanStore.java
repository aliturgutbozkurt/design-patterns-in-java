package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.util.List;

/** Assignment 02 — your store. See assignments/02-library-loans.en.md (Türkçe: 02-library-loans.tr.md). */
public class InMemoryLoanStore implements LoanStore {

    @Override
    public void save(Loan loan) {
        // TODO(ex02): keep the loan (insertion order matters).
        throw new UnsupportedOperationException("TODO(ex02): implement save(Loan)");
    }

    @Override
    public void remove(Loan loan) {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement remove(Loan)");
    }

    @Override
    public List<Loan> activeLoansOf(Member member) {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement activeLoansOf(Member)");
    }

    @Override
    public List<Loan> allActive() {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement allActive()");
    }
}
