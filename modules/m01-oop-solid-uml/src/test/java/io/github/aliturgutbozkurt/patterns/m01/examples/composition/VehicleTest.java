package io.github.aliturgutbozkurt.patterns.m01.examples.composition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after.Engine;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after.Gearbox;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after.Vehicle;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.DieselManualCar;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.PetrolAutomaticCar;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class VehicleTest {

    static final List<Engine> ENGINES = List.of(new Engine.Petrol(50, 6.25), new Engine.Diesel(55, 5), new Engine.Electric(60, 15));

    @Test
    void beforeNeedsOneSubclassPerEngineAndGearbox() {
        var permitted = io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.Vehicle.class
                .getPermittedSubclasses();
        assertThat(permitted).hasSize(2 * 2);
        assertThat(new PetrolAutomaticCar().describe()).isEqualTo("Petrol engine, automatic gearbox, range 800 km");
        assertThat(new DieselManualCar().describe()).isEqualTo("Diesel engine, manual gearbox, range 1100 km");
    }

    @Test
    void afterEveryCombinationIsConstructible() {
        List<Vehicle> all = ENGINES.stream()
                .flatMap(engine -> Stream.of(Gearbox.values()).map(gearbox -> new Vehicle(engine, gearbox)))
                .toList();
        assertThat(all).hasSize(3 * 2);
        assertThat(all).extracting(Vehicle::describe).doesNotHaveDuplicates();
    }

    @Test
    void afterRangeIsComputedPerEngine() {
        assertThat(new Vehicle(new Engine.Petrol(50, 6.25), Gearbox.MANUAL).rangeKm()).isEqualTo(800);
        assertThat(new Vehicle(new Engine.Diesel(55, 5), Gearbox.MANUAL).rangeKm()).isEqualTo(1100);
        assertThat(new Vehicle(new Engine.Electric(60, 15), Gearbox.AUTOMATIC).rangeKm()).isEqualTo(400);
    }

    @Test
    void afterDescribeMatchesTheBeforeText() {
        assertThat(new Vehicle(new Engine.Petrol(50, 6.25), Gearbox.AUTOMATIC).describe())
                .isEqualTo(new PetrolAutomaticCar().describe());
        assertThat(new Vehicle(new Engine.Electric(60, 15), Gearbox.AUTOMATIC).describe())
                .isEqualTo("Electric engine, automatic gearbox, range 400 km");
    }

    @Test
    void afterEnginesValidateTheirParameters() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Engine.Petrol(0, 6));
        assertThatIllegalArgumentException().isThrownBy(() -> new Engine.Electric(60, -1));
    }

    @Test
    void demoComparesBothDesigns() {
        assertThat(Console.capture(() -> VehicleDemo.main(new String[0]))).isEqualTo("""
                == before: one subclass per engine x gearbox ==
                PetrolManualCar: Petrol engine, manual gearbox, range 800 km
                PetrolAutomaticCar: Petrol engine, automatic gearbox, range 800 km
                DieselManualCar: Diesel engine, manual gearbox, range 1100 km
                DieselAutomaticCar: Diesel engine, automatic gearbox, range 1100 km
                subclasses: 4 (an electric engine would need 2 more)
                == after: a Vehicle is composed of an Engine and a Gearbox ==
                Petrol engine, manual gearbox, range 800 km
                Petrol engine, automatic gearbox, range 800 km
                Diesel engine, manual gearbox, range 1100 km
                Diesel engine, automatic gearbox, range 1100 km
                Electric engine, manual gearbox, range 400 km
                Electric engine, automatic gearbox, range 400 km
                combinations: 6 (the electric engine was one new record)
                """);
    }
}
