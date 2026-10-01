package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * GIVEN — do not modify. The result of {@link Payroll#summarize}: the grand total, a total per {@link Kind} and the ids
 * of everyone with the highest monthly pay.
 */
public record PayrollSummary(long totalCents, Map<Kind, Long> totalByKind, List<String> highestPaidIds) {

    public PayrollSummary {
        Objects.requireNonNull(totalByKind, "totalByKind");
        Objects.requireNonNull(highestPaidIds, "highestPaidIds");
    }
}
