package io.github.aliturgutbozkurt.patterns.m01.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Loan;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.LoanStore;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex02.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reference solution for assignment 02: a list keeps insertion order.
 *
 * @see "m01 lesson, section DIP"
 */
public class InMemoryLoanStore implements LoanStore {

    private final List<Loan> loans = new ArrayList<>();

    @Override
    public void save(Loan loan) {
        loans.add(Objects.requireNonNull(loan, "loan"));
    }

    @Override
    public void remove(Loan loan) {
        loans.remove(loan);
    }

    @Override
    public List<Loan> activeLoansOf(Member member) {
        return loans.stream().filter(loan -> loan.member().equals(member)).toList();
    }

    @Override
    public List<Loan> allActive() {
        return List.copyOf(loans);
    }
}
