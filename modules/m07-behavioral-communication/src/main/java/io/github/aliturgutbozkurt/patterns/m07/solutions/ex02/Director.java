package io.github.aliturgutbozkurt.patterns.m07.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approved;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approver;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Decision;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Expense;
import java.util.Optional;

/**
 * Reference solution for assignment 02: approves up to 20 000.00.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public class Director implements Approver {

    private static final long LIMIT_CENTS = 20_000_00;

    @Override
    public String name() {
        return "director";
    }

    @Override
    public Optional<Decision> review(Expense expense) {
        return expense.amountCents() <= LIMIT_CENTS ? Optional.of(new Approved(name())) : Optional.empty();
    }
}
