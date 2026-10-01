package io.github.aliturgutbozkurt.patterns.m01.examples.composition;

import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after.Engine;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after.Gearbox;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after.Vehicle;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.DieselAutomaticCar;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.DieselManualCar;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.PetrolAutomaticCar;
import io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.PetrolManualCar;
import java.util.List;

/**
 * Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/composition/VehicleDemo.java}
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public final class VehicleDemo {

    private VehicleDemo() {}

    public static void main(String[] args) {
        System.out.println("== before: one subclass per engine x gearbox ==");
        List<io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.before.Vehicle> cars = List.of(
                new PetrolManualCar(), new PetrolAutomaticCar(), new DieselManualCar(), new DieselAutomaticCar());
        cars.forEach(car -> System.out.println(car.getClass().getSimpleName() + ": " + car.describe()));
        System.out.println("subclasses: " + cars.size() + " (an electric engine would need 2 more)");

        System.out.println("== after: a Vehicle is composed of an Engine and a Gearbox ==");
        List<Engine> engines = List.of(new Engine.Petrol(50, 6.25), new Engine.Diesel(55, 5), new Engine.Electric(60, 15));
        int combinations = 0;
        for (Engine engine : engines) {
            for (Gearbox gearbox : Gearbox.values()) {
                System.out.println(new Vehicle(engine, gearbox).describe());
                combinations++;
            }
        }
        System.out.println("combinations: " + combinations + " (the electric engine was one new record)");
    }
}
