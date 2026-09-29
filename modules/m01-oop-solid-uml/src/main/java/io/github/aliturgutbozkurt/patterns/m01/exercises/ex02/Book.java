package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

import java.util.Objects;

/** GIVEN — do not modify. A book in the library. */
public record Book(String isbn, String title) {

    public Book {
        Objects.requireNonNull(isbn, "isbn");
        Objects.requireNonNull(title, "title");
    }
}
