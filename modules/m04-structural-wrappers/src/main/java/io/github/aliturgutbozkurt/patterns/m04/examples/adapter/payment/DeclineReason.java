package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

/**
 * Why a payment was declined, in our own vocabulary instead of the legacy numeric codes.
 *
 * @see "m04 lesson, section Adapter"
 */
public enum DeclineReason {
    INSUFFICIENT_FUNDS,
    CARD_EXPIRED,
    UNKNOWN
}
