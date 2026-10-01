package io.github.aliturgutbozkurt.patterns.m09.examples.result.checkout;

/** A request after the coupon step: the amount to charge is now known. */
record Priced(CheckoutRequest request, long totalCents) {}
