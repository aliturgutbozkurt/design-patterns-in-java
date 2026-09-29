package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ShippingStrategyTest {

    private static final Parcel SMALL = new Parcel(0.5, new BigDecimal("20.00"));
    private static final Parcel HEAVY = new Parcel(12.0, new BigDecimal("45.00"));
    private static final Parcel BIG_ORDER = new Parcel(3.0, new BigDecimal("50.00"));

    private final ShippingStrategy flat = new FlatRate(new BigDecimal("4.99"));
    private final ShippingStrategy byWeight = new WeightBased(new BigDecimal("2.00"), new BigDecimal("0.80"));
    private final ShippingStrategy freeOver50 = new FreeOverThreshold(new BigDecimal("50.00"), flat);

    @Test
    void flatRateCostsTheSameForEveryParcel() {
        assertThat(flat.cost(SMALL)).isEqualTo(new BigDecimal("4.99"));
        assertThat(flat.cost(HEAVY)).isEqualTo(new BigDecimal("4.99"));
    }

    @Test
    void weightBasedAddsARatePerKilogram() {
        assertThat(byWeight.cost(SMALL)).isEqualTo(new BigDecimal("2.40"));
        assertThat(byWeight.cost(HEAVY)).isEqualTo(new BigDecimal("11.60"));
        assertThat(byWeight.cost(BIG_ORDER)).isEqualTo(new BigDecimal("4.40"));
    }

    @Test
    void freeShippingThresholdIsInclusive() {
        assertThat(freeOver50.cost(BIG_ORDER)).isEqualTo(new BigDecimal("0.00"));
        assertThat(freeOver50.cost(new Parcel(3.0, new BigDecimal("49.99")))).isEqualTo(new BigDecimal("4.99"));
    }

    @Test
    void swappingTheStrategyChangesTheNextQuote() {
        var calculator = new ShippingCalculator(flat);
        assertThat(calculator.quote(HEAVY)).isEqualTo(new BigDecimal("4.99"));
        calculator.setStrategy(byWeight);
        assertThat(calculator.quote(HEAVY)).isEqualTo(new BigDecimal("11.60"));
        assertThat(calculator.strategy()).isSameAs(byWeight);
    }

    @Test
    void parcelRejectsNegativeWeight() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Parcel(-1.0, BigDecimal.TEN))
                .withMessage("weightKg must be >= 0: -1.0");
    }

    @Test
    void demoPrintsQuotesPerStrategyAndARuntimeSwitch() {
        assertThat(Console.capture(() -> ShippingDemo.main(new String[0]))).isEqualTo("""
                parcel 0.5 kg, order 20.00:  flat 4.99 | per-kg 2.40 | free-over-50.00 4.99
                parcel 12.0 kg, order 45.00: flat 4.99 | per-kg 11.60 | free-over-50.00 4.99
                parcel 3.0 kg, order 50.00:  flat 4.99 | per-kg 4.40 | free-over-50.00 0.00
                checkout uses flat: 4.99
                checkout switched to per-kg: 11.60
                """);
    }
}
