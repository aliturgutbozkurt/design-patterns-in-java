package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import java.util.List;

/** GIVEN — do not modify. Turns raw sales into a {@link SalesSummary}. */
public interface SalesSummarizer {

    /** @throws NullPointerException if {@code sales} is {@code null} */
    SalesSummary summarize(List<Sale> sales);
}
