package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Assignment 02 — payment fees over a sealed hierarchy. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract FeeCalculator calculator();

    private BigDecimal fee(Payment payment) {
        return calculator().feeFor(payment);
    }

    private static BigDecimal eur(String amount) {
        return new BigDecimal(amount);
    }

    @Test
    void domesticCardFee() {
        assertThat(fee(new CardPayment(eur("100.00"), false))).isEqualTo(eur("3.20"));
    }

    @Test
    void internationalCardFee() {
        assertThat(fee(new CardPayment(eur("100.00"), true))).isEqualTo(eur("4.20"));
    }

    @Test
    void bankTransferFlatFee() {
        assertThat(fee(new BankTransfer(eur("999.99"), "TR330006100519786457841326"))).isEqualTo(eur("0.50"));
    }

    @Test
    void bankTransferFreeFromThousand() {
        assertThat(fee(new BankTransfer(eur("1000.00"), "DE89370400440532013000"))).isEqualTo(eur("0.00"));
    }

    @Test
    void walletFeeBelowCap() {
        assertThat(fee(new WalletPayment(eur("200.00"), "PayWallet"))).isEqualTo(eur("3.00"));
    }

    @Test
    void walletFeeCapped() {
        assertThat(fee(new WalletPayment(eur("1000.00"), "PayWallet"))).isEqualTo(eur("5.00"));
    }

    @Test
    void feesUseBankersRounding() {
        // 5.00 × 2.9 % + 0.30 = 0.445 → HALF_EVEN rounds to the even neighbour 0.44 (HALF_UP would give 0.45)
        assertThat(fee(new CardPayment(eur("5.00"), false))).isEqualTo(eur("0.44"));
    }

    @Test
    void rejectsNull() {
        assertThatNullPointerException().isThrownBy(() -> fee(null));
    }
}
