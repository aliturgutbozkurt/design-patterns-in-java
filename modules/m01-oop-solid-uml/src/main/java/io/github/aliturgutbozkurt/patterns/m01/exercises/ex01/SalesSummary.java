package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/** GIVEN — do not modify. Totals per region (alphabetical) and the grand total, all scale 2. */
public record SalesSummary(SortedMap<String, BigDecimal> totalsByRegion, BigDecimal grandTotal) {

    public SalesSummary {
        totalsByRegion = Collections.unmodifiableSortedMap(new TreeMap<>(totalsByRegion));
        Objects.requireNonNull(grandTotal, "grandTotal");
    }
}
