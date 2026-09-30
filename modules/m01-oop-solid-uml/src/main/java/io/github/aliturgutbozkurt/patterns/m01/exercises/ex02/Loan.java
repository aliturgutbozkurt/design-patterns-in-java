package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.time.LocalDate;
import java.util.Objects;

/** GIVEN — do not modify. One book lent to one member until {@code dueDate}. */
public record Loan(Book book, Member member, LocalDate dueDate) {

    public Loan {
        Objects.requireNonNull(book, "book");
        Objects.requireNonNull(member, "member");
        Objects.requireNonNull(dueDate, "dueDate");
    }
}
