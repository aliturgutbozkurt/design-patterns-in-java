package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import java.util.List;

/** GIVEN — do not modify. Inbound port: check out a customer's cart. */
public interface CheckoutUseCase {

    /** Business refusals are a {@link CheckoutResult.Rejected}; infrastructure failures propagate as exceptions. */
    CheckoutResult checkout(String customer, List<CartItem> cart);
}
