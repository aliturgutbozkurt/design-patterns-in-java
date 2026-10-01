package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

/**
 * The checkout input as one value instead of four loose parameters.
 *
 * @param customer who buys
 * @param customerType {@code REGULAR}, {@code VIP} or {@code EMPLOYEE}
 * @param quantity number of items
 * @param unitPriceCents price per item
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public record CheckoutRequest(String customer, String customerType, int quantity, long unitPriceCents) {}
