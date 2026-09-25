package io.github.aliturgutbozkurt.patterns.m00.examples.records;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m00.support.Console;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void normalisesScaleToTheCurrencysFractionDigits() {
        assertThat(Money.of("12.5", "EUR").amount()).isEqualTo(new BigDecimal("12.50"));
        assertThat(Money.of("1200", "JPY").amount().scale()).isZero();
    }

    @Test
    void isEqualByValue() {
        assertThat(Money.of("10", "EUR")).isEqualTo(Money.of("10.00", "EUR")).hasSameHashCodeAs(Money.of("10.0", "EUR"));
        assertThat(Money.of("10", "EUR")).isNotEqualTo(Money.of("10", "USD"));
    }

    @Test
    void addsAmountsOfTheSameCurrency() {
        assertThat(Money.of("12.50", "EUR").plus(Money.of("0.75", "EUR"))).isEqualTo(Money.of("13.25", "EUR"));
    }

    @Test
    void refusesToMixCurrencies() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Money.of("1", "EUR").plus(Money.of("1", "USD")))
                .withMessageContaining("EUR").withMessageContaining("USD");
    }

    @Test
    void multipliesByAQuantity() {
        assertThat(Money.of("2.50", "EUR").times(3)).isEqualTo(Money.of("7.50", "EUR"));
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of("1", "EUR").times(-1));
    }

    @Test
    void rejectsInvalidState() {
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of("-0.01", "EUR"));
        assertThatNullPointerException().isThrownBy(() -> new Money(BigDecimal.ONE, null));
        assertThatNullPointerException().isThrownBy(() -> new Money(null, java.util.Currency.getInstance("EUR")));
    }

    @Test
    void printsAmountAndCurrency() {
        assertThat(Money.of("3", "EUR")).hasToString("3.00 EUR");
    }

    @Test
    void demoPrintsExpectedLines() {
        assertThat(Console.capture(() -> MoneyDemo.main(new String[0]))).isEqualTo("""
                price    = 12.50 EUR
                shipping = 4.99 EUR
                total    = 17.49 EUR
                3 × price = 37.50 EUR
                12.50 EUR equals 12.5 EUR? true
                Cannot add USD to EUR
                """);
    }
}
