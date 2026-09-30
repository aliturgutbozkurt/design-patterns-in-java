package io.github.aliturgutbozkurt.patterns.m01.exercises.ex01;

import java.util.List;

/** GIVEN — do not modify. Produces the finished report for a list of sales. */
public interface ReportGenerator {

    /** @throws NullPointerException if {@code sales} is {@code null} */
    String generate(List<Sale> sales);
}
