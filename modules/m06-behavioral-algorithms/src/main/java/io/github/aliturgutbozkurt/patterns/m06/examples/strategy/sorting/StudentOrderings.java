package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.sorting;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.Comparator;

/**
 * {@link Comparator} is the JDK's best-known Strategy: {@code List.sort} is the context, and these named constants are
 * strategies built by composing smaller ones.
 *
 * @see "m06 lesson, section Strategy"
 */
public final class StudentOrderings {

    public static final Comparator<Student> BY_NAME = comparing(Student::name);

    public static final Comparator<Student> BY_YEAR = comparingInt(Student::year);

    public static final Comparator<Student> BY_GPA_DESC_THEN_NAME =
            comparingDouble(Student::gpa).reversed().thenComparing(Student::name);

    public static final Comparator<Student> BY_ADVISOR_NULLS_LAST_THEN_NAME =
            comparing(Student::advisor, nullsLast(naturalOrder())).thenComparing(BY_NAME);

    private StudentOrderings() {}
}
