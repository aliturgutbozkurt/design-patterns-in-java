package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.util.List;

/** GIVEN — do not modify. Where active loans are kept. Lists are returned in the order the loans were saved. */
public interface LoanStore {

    void save(Loan loan);

    /** Removes the loan; does nothing if it is not stored. */
    void remove(Loan loan);

    List<Loan> activeLoansOf(Member member);

    List<Loan> allActive();
}
