package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

import java.util.Objects;

/** GIVEN — do not modify. Replace the {@code length} characters at index {@code position} with {@code text}. */
public record Replace(int position, int length, String text) implements Edit {

    public Replace {
        Objects.requireNonNull(text, "text");
    }
}
