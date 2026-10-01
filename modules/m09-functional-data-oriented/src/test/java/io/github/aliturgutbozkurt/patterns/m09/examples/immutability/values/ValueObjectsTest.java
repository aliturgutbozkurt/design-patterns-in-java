package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.values;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    private static final LocalDate MAR_1 = LocalDate.of(2027, 3, 1);
    private static final LocalDate MAR_10 = LocalDate.of(2027, 3, 10);

    @Test
    void moneyWithTheSameValueIsEqualEvenWhenTheRawBigDecimalsAreNot() {
        assertThat(new BigDecimal("2.0")).isNotEqualTo(new BigDecimal("2.00"));
        assertThat(Money.of("2.0", "EUR")).isEqualTo(Money.of("2.00", "EUR"));
        assertThat(Money.of("2.0", "EUR")).hasSameHashCodeAs(Money.of("2.00", "EUR"));
    }

    @Test
    void scaleFollowsTheCurrency() {
        assertThat(Money.of("150", "JPY").amount().scale()).isZero();
        assertThat(Money.of("150", "EUR").amount().scale()).isEqualTo(2);
        assertThat(Money.of("150", "TRY")).hasToString("150.00 TRY");
    }

    @Test
    void roundingIsHalfEven() {
        assertThat(Money.of("2.345", "EUR")).hasToString("2.34 EUR");
        assertThat(Money.of("2.355", "EUR")).hasToString("2.36 EUR");
    }

    @Test
    void operationsReturnNewValues() {
        var price = Money.of("19.99", "EUR");
        assertThat(price.plus(Money.of("0.01", "EUR"))).isEqualTo(Money.of("20", "EUR"));
        assertThat(price.times(3)).isEqualTo(Money.of("59.97", "EUR"));
        assertThat(price.percent(15)).isEqualTo(Money.of("3.00", "EUR"));
        assertThat(price).isEqualTo(Money.of("19.99", "EUR"));
    }

    @Test
    void addingDifferentCurrenciesThrows() {
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of("1", "EUR").plus(Money.of("1", "USD")))
                .withMessage("currency mismatch: EUR vs USD");
    }

    @Test
    void dateRangeRejectsAnEndBeforeTheStart() {
        assertThatIllegalArgumentException().isThrownBy(() -> new DateRange(MAR_10, MAR_1))
                .withMessage("endInclusive 2027-03-01 is before start 2027-03-10");
    }

    @Test
    void daysAreCountedInclusively() {
        assertThat(new DateRange(MAR_1, MAR_10).days()).isEqualTo(10);
        assertThat(new DateRange(MAR_1, MAR_1).days()).isEqualTo(1);
    }

    @Test
    void touchingRangesOverlap() {
        var march = new DateRange(MAR_1, MAR_10);
        assertThat(march.overlaps(new DateRange(MAR_10, MAR_10.plusDays(5)))).isTrue();
        assertThat(march.overlaps(new DateRange(MAR_10.plusDays(1), MAR_10.plusDays(5)))).isFalse();
        assertThat(new DateRange(MAR_1.minusDays(9), MAR_1).overlaps(march)).isTrue();
    }

    @Test
    void withEndAndShiftedByLeaveTheOriginalUnchanged() {
        var march = new DateRange(MAR_1, MAR_10);
        assertThat(march.withEnd(MAR_10.plusDays(1))).isEqualTo(new DateRange(MAR_1, LocalDate.of(2027, 3, 11)));
        assertThat(march.shiftedBy(Period.ofMonths(1)))
                .isEqualTo(new DateRange(LocalDate.of(2027, 4, 1), LocalDate.of(2027, 4, 10)));
        assertThat(march).isEqualTo(new DateRange(MAR_1, MAR_10));
    }

    @Test
    void aMutatedKeyIsLostInsideItsHashSet() {
        var key = new MutableKeyPitfall("SPRING10");
        var set = new HashSet<MutableKeyPitfall>();
        set.add(key);
        key.setCode("SPRING20");
        assertThat(set.contains(key)).isFalse();
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next()).isSameAs(key);
    }

    @Test
    void demoPrintsValueEqualityRoundingRangesAndTheMutableKeyBug() {
        assertThat(Console.capture(() -> ValueObjectsDemo.main(new String[0]))).isEqualTo("""
                -- BigDecimal vs. Money
                new BigDecimal("2.0").equals(new BigDecimal("2.00")) = false
                Money.of("2.0", "EUR").equals(Money.of("2.00", "EUR")) = true
                2.345 EUR -> 2.34 EUR, 2.355 EUR -> 2.36 EUR (HALF_EVEN)
                150 JPY -> 150 JPY (no minor unit)
                19.99 EUR x 3 = 59.97 EUR, 15% of it = 9.00 EUR
                -- DateRange on java.time.LocalDate
                spring sale 2027-03-01..2027-03-10: 10 day(s)
                overlaps 2027-03-10..2027-03-15: true
                extended to 2027-03-11, shifted by P1M: 2027-04-01..2027-04-10
                original still 2027-03-01..2027-03-10
                -- a mutable key breaks a HashSet
                after setCode: contains(key) = false, size = 1
                """);
    }
}
