package io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * One student's scores — data plus the one calculation that belongs to it.
 *
 * @see "m01 lesson, section SRP"
 */
public record StudentScores(String name, List<BigDecimal> scores) {

    public StudentScores {
        Objects.requireNonNull(name, "name");
        scores = List.copyOf(scores);
        if (scores.isEmpty()) {
            throw new IllegalArgumentException("at least one score is required for " + name);
        }
    }

    /** Arithmetic mean, rounded half-even to two decimals. */
    public BigDecimal average() {
        BigDecimal sum = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_EVEN);
    }
}
