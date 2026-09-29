package io.github.aliturgutbozkurt.patterns.m07.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approved;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Approver;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Category;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Decision;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex02.Expense;
import java.util.Optional;

/**
 * Reference solution for assignment 02: approves up to 500.00, but never equipment.
 *
 * @see "m07 lesson, section Chain of Responsibility"
 */
public class TeamLead implements Approver {

    private static final long LIMIT_CENTS = 500_00;

    @Override
    public String name() {
        return "team lead";
    }

    @Override
    public Optional<Decision> review(Expense expense) {
        boolean canApprove = expense.category() != Category.EQUIPMENT && expense.amountCents() <= LIMIT_CENTS;
        return canApprove ? Optional.of(new Approved(name())) : Optional.empty();
    }
}
