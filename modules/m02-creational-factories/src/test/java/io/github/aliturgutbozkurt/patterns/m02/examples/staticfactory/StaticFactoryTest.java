package io.github.aliturgutbozkurt.patterns.m02.examples.staticfactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.within;

import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StaticFactoryTest {

    @Test
    void moneyFactoriesAgreeWithTheConstructor() {
        var viaConstructor = new Money(new BigDecimal("12.50"), Currency.getInstance("EUR"));
        assertThat(Money.of("12.5", "EUR")).isEqualTo(viaConstructor);
        assertThat(Money.parse("12.50 EUR")).isEqualTo(viaConstructor);
        assertThat(Money.zero("TRY")).isEqualTo(Money.of("0", "TRY"));
    }

    @Test
    void moneyParseRejectsGarbage() {
        assertThatIllegalArgumentException().isThrownBy(() -> Money.parse("twelve euros"))
                .withMessage("cannot parse money: 'twelve euros' (expected '<amount> <currency>')");
        assertThatIllegalArgumentException().isThrownBy(() -> Money.parse("12.50"));
    }

    @Test
    void temperatureFactoriesAreNamedByUnit() {
        assertThat(Temperature.ofCelsius(0).kelvin()).isCloseTo(273.15, within(1e-9));
        assertThat(Temperature.ofFahrenheit(32).celsius()).isCloseTo(0, within(1e-9));
        assertThat(Temperature.ofKelvin(373.15).fahrenheit()).isCloseTo(212, within(1e-9));
        assertThatIllegalArgumentException().isThrownBy(() -> Temperature.ofKelvin(-1));
    }

    @Test
    void percentageInstancesAreCached() {
        assertThat(Percentage.of(15)).isSameAs(Percentage.of(15));
        assertThat(Percentage.of(0)).isSameAs(Percentage.of(0));
        assertThat(Percentage.of(100)).hasToString("100%");
        assertThat(Percentage.of(15).applyTo(new BigDecimal("200"))).isEqualTo(new BigDecimal("30.00"));
        assertThatIllegalArgumentException().isThrownBy(() -> Percentage.of(101));
    }

    @ParameterizedTest
    @CsvSource({"1, Letter, 2.50", "500, Letter, 2.50", "501, Parcel, 6.50", "30000, Parcel, 21.00",
                "30001, Freight, 49.30"})
    void shipmentFactoryPicksTheSubtypeFromTheWeight(int grams, String type, String price) {
        Shipment shipment = Shipment.forWeight(grams);
        assertThat(shipment.getClass().getSimpleName()).isEqualTo(type);
        assertThat(shipment.price()).isEqualTo(new BigDecimal(price));
    }

    @Test
    void shipmentRejectsNonPositiveWeight() {
        assertThatIllegalArgumentException().isThrownBy(() -> Shipment.forWeight(0));
    }

    @Test
    void demoPrintsEveryKindOfFactory() {
        assertThat(Console.capture(() -> StaticFactoryDemo.main(new String[0]))).isEqualTo("""
                == named factories ==
                Money.of("12.5", "EUR") = 12.50 EUR
                Money.parse("99.90 TRY") = 99.90 TRY
                Money.zero("USD") = 0.00 USD
                ofCelsius(100) = 212.0 °F
                ofFahrenheit(212) = 100.0 °C
                == instance caching ==
                Percentage.of(15) == Percentage.of(15)? true
                == choosing a subtype ==
                300 g -> Letter 2.50
                2500 g -> Parcel 7.50
                45000 g -> Freight 53.50
                """);
    }
}
