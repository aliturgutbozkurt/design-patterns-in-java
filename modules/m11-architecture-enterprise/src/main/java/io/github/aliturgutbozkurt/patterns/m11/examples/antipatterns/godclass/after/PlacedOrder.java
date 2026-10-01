package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.godclass.after;

/**
 * A confirmed order as the repository stores it.
 *
 * @param id order id
 * @param customer who bought
 * @param quantity number of items
 * @param totalCents price paid
 * @see "m11 lesson, section Anti-patterns — god class"
 */
public record PlacedOrder(String id, String customer, int quantity, long totalCents) {}
