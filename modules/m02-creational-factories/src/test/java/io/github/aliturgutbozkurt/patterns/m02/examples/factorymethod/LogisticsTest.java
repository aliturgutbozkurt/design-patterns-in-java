package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Cargo;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Logistics;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.RoadLogistics;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.SeaLogistics;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Ship;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Transport;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Truck;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LogisticsTest {

    static final Cargo PARTS = new Cargo("Machine parts", 2000, 1200);

    @Test
    void roadLogisticsPlansWithATruck() {
        assertThat(new RoadLogistics().planDelivery(PARTS))
                .isEqualTo("Road: Machine parts by Truck, 1200 km, cost 1440.00, 2 day(s)");
    }

    @Test
    void seaLogisticsPlansWithAShip() {
        assertThat(new SeaLogistics().planDelivery(PARTS))
                .isEqualTo("Sea: Machine parts by Ship, 1200 km, cost 980.00, 5 day(s)");
    }

    @Test
    void transportsComputeCostAndDays() {
        assertThat(new Truck().cost(PARTS)).isEqualTo(new BigDecimal("1440.00"));
        assertThat(new Truck().days(new Cargo("x", 1, 800))).isEqualTo(1);
        assertThat(new Ship().days(new Cargo("x", 1, 501))).isEqualTo(4);
    }

    /** A new mode is a new creator subclass plus a new product; {@code Logistics} itself is untouched. */
    @Test
    void aNewModeNeedsNoChangeToLogistics() {
        Transport plane = new Transport() {
            @Override public String name() { return "Plane"; }
            @Override public BigDecimal cost(Cargo cargo) { return new BigDecimal("5000.00"); }
            @Override public int days(Cargo cargo) { return 1; }
        };
        Logistics air = new Logistics("Air") {
            @Override protected Transport createTransport() { return plane; }
        };
        assertThat(air.planDelivery(PARTS)).isEqualTo("Air: Machine parts by Plane, 1200 km, cost 5000.00, 1 day(s)");
    }

    @Test
    void cargoValidates() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Cargo("x", 0, 10));
        assertThatIllegalArgumentException().isThrownBy(() -> new Cargo("x", 10, 0));
    }

    @Test
    void demoPlansBothModes() {
        assertThat(Console.capture(() -> LogisticsDemo.main(new String[0]))).isEqualTo("""
                Road: Machine parts by Truck, 1200 km, cost 1440.00, 2 day(s)
                Sea: Machine parts by Ship, 1200 km, cost 980.00, 5 day(s)
                """);
    }
}
