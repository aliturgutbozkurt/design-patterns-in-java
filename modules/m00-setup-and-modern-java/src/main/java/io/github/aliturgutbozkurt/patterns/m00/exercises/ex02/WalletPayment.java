package io.github.aliturgutbozkurt.patterns.m00.exercises.ex02;

import java.math.BigDecimal;
import java.util.Objects;

/** GIVEN — do not modify. A payment through a digital wallet provider. */
public record WalletPayment(BigDecimal amount, String provider) implements Payment {

    public WalletPayment {
        Payment.requirePositive(amount);
        Objects.requireNonNull(provider, "provider");
    }
}
