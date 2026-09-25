package io.github.aliturgutbozkurt.patterns.m00.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

/** Assignment 01 — Temperature value object. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    /** Creates the student's (or the reference) temperature. */
    protected abstract Temperature of(double value, Unit unit);

    @Test
    void isARecord() {
        assertThat(of(20, Unit.CELSIUS).getClass().isRecord()).isTrue();
    }

    @Test
    void convertsCelsiusToFahrenheit() {
        Temperature fahrenheit = of(100, Unit.CELSIUS).to(Unit.FAHRENHEIT);
        assertThat(fahrenheit.unit()).isEqualTo(Unit.FAHRENHEIT);
        assertThat(fahrenheit.value()).isCloseTo(212, within(1e-9));
        assertThat(of(-40, Unit.CELSIUS).to(Unit.FAHRENHEIT).value()).isCloseTo(-40, within(1e-9));
    }

    @Test
    void convertsFahrenheitToKelvin() {
        Temperature kelvin = of(32, Unit.FAHRENHEIT).to(Unit.KELVIN);
        assertThat(kelvin.unit()).isEqualTo(Unit.KELVIN);
        assertThat(kelvin.value()).isCloseTo(273.15, within(1e-9));
    }

    @Test
    void convertingToSameUnitReturnsEqualValue() {
        Temperature original = of(21.5, Unit.CELSIUS);
        assertThat(original.to(Unit.CELSIUS)).isEqualTo(original);
    }

    @Test
    void rejectsTemperaturesBelowAbsoluteZero() {
        assertThatIllegalArgumentException().isThrownBy(() -> of(-273.16, Unit.CELSIUS));
        assertThatIllegalArgumentException().isThrownBy(() -> of(-460, Unit.FAHRENHEIT));
        assertThatIllegalArgumentException().isThrownBy(() -> of(-0.01, Unit.KELVIN));
        assertThatCode(() -> of(-273.15, Unit.CELSIUS)).doesNotThrowAnyException();
    }

    @Test
    void rejectsNaN() {
        assertThatIllegalArgumentException().isThrownBy(() -> of(Double.NaN, Unit.CELSIUS));
    }

    @Test
    void rejectsNullUnit() {
        assertThatNullPointerException().isThrownBy(() -> of(20, null));
    }

    @Test
    void equalWhenValueAndUnitEqual() {
        assertThat(of(20, Unit.CELSIUS)).isEqualTo(of(20, Unit.CELSIUS)).hasSameHashCodeAs(of(20, Unit.CELSIUS));
        assertThat(of(20, Unit.CELSIUS)).isNotEqualTo(of(68, Unit.FAHRENHEIT));
    }
}
