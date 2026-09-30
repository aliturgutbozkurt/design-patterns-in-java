package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. Insert {@code text} before index {@code position} ({@code position == length} appends). */
public record Insert(int position, String text) implements Edit {

    public Insert {
        Objects.requireNonNull(text, "text");
    }
}
