package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

/**
 * Target: the payment interface our checkout code is written against.
 *
 * @see "m04 lesson, section Adapter"
 */
public interface PaymentProcessor {

    PaymentResult pay(PaymentRequest request);
}
