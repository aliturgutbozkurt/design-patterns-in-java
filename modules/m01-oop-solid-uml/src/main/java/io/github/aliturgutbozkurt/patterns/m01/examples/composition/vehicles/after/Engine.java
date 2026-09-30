package io.github.aliturgutbozkurt.patterns.m01.examples.composition.vehicles.after;

/**
 * One dimension of a vehicle. Adding an engine is one new record, whatever the number of gearboxes.
 *
 * @see "m01 lesson, section Composition over inheritance"
 */
public sealed interface Engine {

    /** A petrol engine: tank size and consumption in litres per 100 km. */
    record Petrol(double tankLitres, double litresPer100Km) implements Engine {
        public Petrol {
            requirePositive("tankLitres", tankLitres);
            requirePositive("litresPer100Km", litresPer100Km);
        }
    }

    /** A diesel engine: tank size and consumption in litres per 100 km. */
    record Diesel(double tankLitres, double litresPer100Km) implements Engine {
        public Diesel {
            requirePositive("tankLitres", tankLitres);
            requirePositive("litresPer100Km", litresPer100Km);
        }
    }

    /** An electric motor: battery size and consumption in kWh per 100 km. */
    record Electric(double batteryKwh, double kwhPer100Km) implements Engine {
        public Electric {
            requirePositive("batteryKwh", batteryKwh);
            requirePositive("kwhPer100Km", kwhPer100Km);
        }
    }

    private static void requirePositive(String name, double value) {
        if (!(value > 0)) {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
    }
}
