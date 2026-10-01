package io.github.aliturgutbozkurt.patterns.m07.examples.mediator;

import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc.Aircraft;
import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc.ControlTower;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/mediator/AirTrafficDemo.java}
 *
 * @see "m07 lesson, section Mediator"
 */
public final class AirTrafficDemo {

    private AirTrafficDemo() {}

    public static void main(String[] args) {
        var tower = new ControlTower(System.out::println);
        var tk1 = new Aircraft("TK1", tower);
        var lh2 = new Aircraft("LH2", tower);
        var ba3 = new Aircraft("BA3", tower);
        var af4 = new Aircraft("AF4", tower);

        tk1.requestLanding();
        lh2.requestLanding();
        ba3.requestTakeoff();
        af4.declareEmergency();
        tk1.vacateRunway();
        af4.vacateRunway();
        lh2.vacateRunway();
        ba3.vacateRunway();
    }
}
