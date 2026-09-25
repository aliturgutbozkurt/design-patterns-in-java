package io.github.aliturgutbozkurt.patterns.m00.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.BankTransfer;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.CardPayment;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.FeeCalculator;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.Payment;
import io.github.aliturgutbozkurt.patterns.m00.exercises.ex02.WalletPayment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Reference solution for assignment 02: one exhaustive switch over the sealed {@link Payment}. */
public class PaymentFees implements FeeCalculator {

    private static final BigDecimal CARD_RATE = new BigDecimal("0.029");
    private static final BigDecimal CARD_FIXED = new BigDecimal("0.30");
    private static final BigDecimal INTERNATIONAL_SURCHARGE = new BigDecimal("0.01");
    private static final BigDecimal TRANSFER_FEE = new BigDecimal("0.50");
    private static final BigDecimal FREE_TRANSFER_FROM = new BigDecimal("1000");
    private static final BigDecimal WALLET_RATE = new BigDecimal("0.015");
    private static final BigDecimal WALLET_CAP = new BigDecimal("5.00");

    @Override
    public BigDecimal feeFor(Payment payment) {
        Objects.requireNonNull(payment, "payment");
        BigDecimal fee = switch (payment) {
            case CardPayment(BigDecimal amount, boolean international) when international ->
                    amount.multiply(CARD_RATE.add(INTERNATIONAL_SURCHARGE)).add(CARD_FIXED);
            case CardPayment(BigDecimal amount, _) -> amount.multiply(CARD_RATE).add(CARD_FIXED);
            case BankTransfer(BigDecimal amount, _) when amount.compareTo(FREE_TRANSFER_FROM) >= 0 -> BigDecimal.ZERO;
            case BankTransfer _ -> TRANSFER_FEE;
            case WalletPayment(BigDecimal amount, _) -> amount.multiply(WALLET_RATE).min(WALLET_CAP);
        };
        return fee.setScale(2, RoundingMode.HALF_EVEN);
    }
}
