package io.github.aliturgutbozkurt.patterns.m07.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. An expense claim; amounts are in cents ({@code 50_000} is 500.00). */
public record Expense(String id, String employee, Category category, long amountCents) {

    public Expense {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(employee, "employee");
        Objects.requireNonNull(category, "category");
    }
}
