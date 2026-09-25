package io.github.aliturgutbozkurt.patterns.m00.exercises.ex01;

/** GIVEN — do not modify. A temperature: an immutable value in one unit. */
public interface Temperature {

    double value();

    Unit unit();

    /**
     * The same temperature expressed in {@code target}. Converting to the unit it already has returns an equal value.
     *
     * @throws NullPointerException if {@code target} is {@code null}
     */
    Temperature to(Unit target);
}
