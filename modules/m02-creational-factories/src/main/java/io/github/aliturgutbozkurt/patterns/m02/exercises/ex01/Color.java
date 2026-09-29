package io.github.aliturgutbozkurt.patterns.m02.exercises.ex01;

/** GIVEN — do not modify. An RGB colour; components are 0–255. */
public interface Color {

    int red();

    int green();

    int blue();

    /** Upper-case {@code #RRGGBB}, e.g. {@code #0A0B0C}. */
    String toHex();
}
