package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

/** GIVEN — do not modify. Asks every provider for a price of {@code sku}, concurrently and within a deadline. */
public interface PriceComparator {

    Comparison compare(String sku) throws InterruptedException;
}
