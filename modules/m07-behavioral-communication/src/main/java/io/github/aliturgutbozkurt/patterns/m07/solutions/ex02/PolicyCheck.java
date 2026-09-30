package io.github.aliturgutbozkurt.patterns.m07.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approver;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Category;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Decision;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Expense;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Rejected;
import java.util.Optional;

/**
 * Reference solution for assignment 02: a link that can only reject — everything it accepts is passed on.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public class PolicyCheck implements Approver {

    private static final long MEALS_LIMIT_CENTS = 100_00;

    @Override
    public String name() {
        return "policy check";
    }

    @Override
    public Optional<Decision> review(Expense expense) {
        if (expense.amountCents() <= 0) {
            return Optional.of(new Rejected(name(), "amount must be positive"));
        }
        if (expense.category() == Category.MEALS && expense.amountCents() > MEALS_LIMIT_CENTS) {
            return Optional.of(new Rejected(name(), "meals above 100.00"));
        }
        return Optional.empty();
    }
}
