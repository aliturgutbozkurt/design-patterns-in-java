package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.sorting;

import java.util.Objects;

/**
 * A student on the course roster; {@code advisor} is {@code null} when none has been assigned yet.
 *
 * @see "m06 lesson, section Strategy"
 */
public record Student(String name, double gpa, int year, String advisor) {

    public Student {
        Objects.requireNonNull(name, "name");
    }
}
