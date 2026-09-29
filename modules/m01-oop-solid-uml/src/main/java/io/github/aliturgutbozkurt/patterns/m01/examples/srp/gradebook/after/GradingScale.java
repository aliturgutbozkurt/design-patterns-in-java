package io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after;

import java.math.BigDecimal;
import java.util.List;

/**
 * Responsibility: the grading policy. Changes only when the faculty changes the letter boundaries.
 *
 * @see "m01 lesson, section SRP"
 */
public final class GradingScale {

    private record Boundary(BigDecimal minimum, String letter) {}

    private static final List<Boundary> BOUNDARIES = List.of(
            new Boundary(BigDecimal.valueOf(90), "AA"),
            new Boundary(BigDecimal.valueOf(85), "BA"),
            new Boundary(BigDecimal.valueOf(80), "BB"),
            new Boundary(BigDecimal.valueOf(75), "CB"),
            new Boundary(BigDecimal.valueOf(70), "CC"),
            new Boundary(BigDecimal.valueOf(65), "DC"),
            new Boundary(BigDecimal.valueOf(60), "DD"),
            new Boundary(BigDecimal.valueOf(50), "FD"));

    /** Letter grade for an average in {@code [0, 100]}. */
    public String letterFor(BigDecimal average) {
        if (average.signum() < 0 || average.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("average out of range 0-100: " + average);
        }
        return BOUNDARIES.stream()
                .filter(boundary -> average.compareTo(boundary.minimum()) >= 0)
                .map(Boundary::letter)
                .findFirst()
                .orElse("FF");
    }
}
