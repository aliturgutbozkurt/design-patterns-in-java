package io.github.aliturgutbozkurt.patterns.m06.exercises.ex01;

/** GIVEN — do not modify. Remove {@code length} characters starting at index {@code position}. */
public record Delete(int position, int length) implements Edit {}
