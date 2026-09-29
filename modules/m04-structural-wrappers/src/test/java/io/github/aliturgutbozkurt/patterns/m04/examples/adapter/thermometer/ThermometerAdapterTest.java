package io.github.aliturgutbozkurt.patterns.m04.examples.adapter.thermometer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import org.junit.jupiter.api.Test;

class ThermometerAdapterTest {

    @Test
    void convertsFahrenheitReadingsToCelsius() {
        CelsiusThermometer thermometer = new FahrenheitAdapter(new FahrenheitSensor(212.0, 32.0, -40.0));
        assertThat(thermometer.readCelsius()).isEqualTo(100.0);
        assertThat(thermometer.readCelsius()).isEqualTo(0.0);
        assertThat(thermometer.readCelsius()).isEqualTo(-40.0);
    }

    @Test
    void classAndLambdaAdaptersGiveIdenticalReadings() {
        CelsiusThermometer classic = new FahrenheitAdapter(new FahrenheitSensor(212.0, 98.6, -40.0));
        FahrenheitSensor sensor = new FahrenheitSensor(212.0, 98.6, -40.0);
        CelsiusThermometer lambda = () -> FahrenheitAdapter.toCelsius(sensor.readFahrenheit());
        for (int i = 0; i < 3; i++) {
            assertThat(lambda.readCelsius()).isEqualTo(classic.readCelsius());
        }
    }

    @Test
    void readsTheSensorOnEveryCall() {
        FahrenheitSensor sensor = new FahrenheitSensor(50.0, 68.0);
        CelsiusThermometer thermometer = new FahrenheitAdapter(sensor);
        thermometer.readCelsius();
        thermometer.readCelsius();
        thermometer.readCelsius();
        assertThat(sensor.readCount()).isEqualTo(3);
    }

    @Test
    void sensorReplaysItsReadingsInALoop() {
        FahrenheitSensor sensor = new FahrenheitSensor(50.0, 68.0);
        assertThat(sensor.readFahrenheit()).isEqualTo(50.0);
        assertThat(sensor.readFahrenheit()).isEqualTo(68.0);
        assertThat(sensor.readFahrenheit()).isEqualTo(50.0);
    }

    @Test
    void rejectsAMissingSensor() {
        assertThatNullPointerException().isThrownBy(() -> new FahrenheitAdapter(null));
    }

    @Test
    void demoPrintsIdenticalReadingsFromBothAdapters() {
        assertThat(Console.capture(() -> ThermometerAdapterDemo.main(new String[0]))).isEqualTo("""
                adapter class:  100.0 °C, 0.0 °C, -40.0 °C
                lambda adapter: 100.0 °C, 0.0 °C, -40.0 °C
                """);
    }
}
