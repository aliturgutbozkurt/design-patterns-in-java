package io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.modern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.FlatRate;
import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.FreeOverThreshold;
import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.Parcel;
import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.ShippingStrategy;
import io.github.aliturgutbozkurt.patterns.m06.examples.strategy.shipping.classic.WeightBased;
import io.github.aliturgutbozkurt.patterns.m06.support.Console;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShippingRuleTest {

    private static final List<Parcel> PARCELS = List.of(
            new Parcel(0.5, new BigDecimal("20.00")),
            new Parcel(12.0, new BigDecimal("45.00")),
            new Parcel(3.0, new BigDecimal("50.00")),
            new Parcel(3.0, new BigDecimal("49.99")),
            new Parcel(0.0, BigDecimal.ZERO));

    @Test
    void lambdasQuoteTheSameAsTheClassicClasses() {
        ShippingStrategy classicFlat = new FlatRate(new BigDecimal("4.99"));
        List<ShippingStrategy> classic = List.of(
                classicFlat,
                new WeightBased(new BigDecimal("2.00"), new BigDecimal("0.80")),
                new FreeOverThreshold(new BigDecimal("50.00"), classicFlat));
        List<ShippingRule> modern = List.of(ShippingOption.FLAT, ShippingOption.PER_KG, ShippingOption.FREE_OVER_50);
        for (Parcel parcel : PARCELS) {
            for (int i = 0; i < classic.size(); i++) {
                assertThat(modern.get(i).cost(parcel)).as("%s for %s", modern.get(i), parcel)
                        .isEqualTo(classic.get(i).cost(parcel));
            }
        }
    }

    @Test
    void cheapestOfPicksTheMinimum() {
        ShippingRule cheapest = ShippingRules.cheapestOf(ShippingOption.FLAT, ShippingOption.PER_KG);
        assertThat(cheapest.cost(PARCELS.get(0))).isEqualTo(new BigDecimal("2.40"));  // per-kg wins
        assertThat(cheapest.cost(PARCELS.get(1))).isEqualTo(new BigDecimal("4.99"));  // flat wins
    }

    @Test
    void cheapestOfNeedsAtLeastOneRule() {
        assertThatIllegalArgumentException().isThrownBy(ShippingRules::cheapestOf);
    }

    @Test
    void aNewRuleIsJustALambda() {
        ShippingRule halfPrice = parcel -> ShippingOption.FLAT.cost(parcel).divide(BigDecimal.TWO);
        assertThat(halfPrice.cost(PARCELS.get(0))).isEqualTo(new BigDecimal("2.495"));
    }

    @Test
    void valueOfRoundTripsEachOption() {
        for (ShippingOption option : ShippingOption.values()) {
            assertThat(ShippingOption.valueOf(option.name())).isSameAs(option);
        }
    }

    @Test
    void demoPrintsQuotesPerOptionAndTheCheapest() {
        assertThat(Console.capture(() -> ShippingDemo.main(new String[0]))).isEqualTo("""
                parcel 0.5 kg, order 20.00:  FLAT 4.99 | PER_KG 2.40 | FREE_OVER_50 4.99 | cheapest 2.40
                parcel 12.0 kg, order 45.00: FLAT 4.99 | PER_KG 11.60 | FREE_OVER_50 4.99 | cheapest 4.99
                parcel 3.0 kg, order 50.00:  FLAT 4.99 | PER_KG 4.40 | FREE_OVER_50 0.00 | cheapest 0.00
                express (lambda, 9.90 + 1.00/kg) for 3.0 kg: 12.90
                """);
    }

    @Test
    void weightBasedRuleRoundsHalfEvenLikeTheRestOfTheCourse() {
        // 2.00 + 0.05 × 0.5 = 2.025 → HALF_EVEN gives 2.02 (HALF_UP would give 2.03)
        ShippingRule cheapPerKg = ShippingRules.weightBased(new BigDecimal("2.00"), new BigDecimal("0.05"));
        assertThat(cheapPerKg.cost(new Parcel(0.5, new BigDecimal("10.00")))).isEqualTo(new BigDecimal("2.02"));
    }
}
