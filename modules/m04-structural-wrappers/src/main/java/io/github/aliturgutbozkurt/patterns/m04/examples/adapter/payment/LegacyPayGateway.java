package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.payment;

import java.math.BigDecimal;

/**
 * Adaptee: a fictional legacy payment SDK we cannot change — decimal strings in, numeric status codes out.
 * The fake decides by the card's last four digits: {@code 0051}, {@code 0054} and {@code 0096} fail, all others pass.
 *
 * @see "m04 lesson, section Adapter"
 */
public class LegacyPayGateway {

    public static final int OK = 0;
    public static final int INSUFFICIENT_FUNDS = 51;
    public static final int CARD_EXPIRED = 54;
    public static final int SYSTEM_MALFUNCTION = 96;

    private int nextTransaction = 1001;
    private String lastTransactionId = "";

    /** Charges {@code amount} (e.g. {@code "12.50"}) and returns a status code; see {@link #lastTransactionId()}. */
    public int makePayment(String cardNumber, String amount, String currencyCode) {
        new BigDecimal(amount);                                       // the SDK only checks the string parses
        int status = switch (cardNumber.substring(cardNumber.length() - 4)) {
            case "0051" -> INSUFFICIENT_FUNDS;
            case "0054" -> CARD_EXPIRED;
            case "0096" -> SYSTEM_MALFUNCTION;
            default -> OK;
        };
        lastTransactionId = status == OK ? "TX-" + nextTransaction++ : "";
        return status;
    }

    /** The id of the last approved payment — the legacy way of returning a second value. */
    public String lastTransactionId() {
        return lastTransactionId;
    }
}
