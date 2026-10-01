package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

/** GIVEN — do not modify. A run-time value: a number or a boolean. */
public sealed interface Value {

    record NumValue(long value) implements Value {}

    record BoolValue(boolean value) implements Value {}
}
