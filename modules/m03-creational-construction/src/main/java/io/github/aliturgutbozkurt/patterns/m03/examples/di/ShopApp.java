package io.github.aliturgutbozkurt.patterns.m03.examples.di;

/**
 * The wired application: the entry points a {@code main} or a test needs.
 *
 * @see "m03 lesson, section Dependency injection as creation"
 */
public record ShopApp(CheckoutService checkout, OrderRepository orders) {}
