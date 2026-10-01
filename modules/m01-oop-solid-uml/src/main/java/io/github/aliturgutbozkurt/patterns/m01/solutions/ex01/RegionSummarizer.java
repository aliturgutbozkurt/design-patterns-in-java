package io.github.aliturgutbozkurt.patterns.m01.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.Sale;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.SalesSummarizer;
import io.github.aliturgutbozkurt.patterns.m01.exercises.ex01.SalesSummary;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Reference solution for assignment 01: only the money rules — how sales add up.
 *
 * @see "m01 lesson, section SRP"
 */
public class RegionSummarizer implements SalesSummarizer {

    @Override
    public SalesSummary summarize(List<Sale> sales) {
        Objects.requireNonNull(sales, "sales");
        SortedMap<String, BigDecimal> totals = new TreeMap<>();
        BigDecimal grandTotal = new BigDecimal("0.00");
        for (Sale sale : sales) {
            totals.merge(sale.region(), sale.amount(), BigDecimal::add);
            grandTotal = grandTotal.add(sale.amount());
        }
        return new SalesSummary(totals, grandTotal);
    }
}
