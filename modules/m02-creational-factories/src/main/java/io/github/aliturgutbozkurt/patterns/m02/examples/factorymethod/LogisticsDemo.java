package io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod;

import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Cargo;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.Logistics;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.RoadLogistics;
import io.github.aliturgutbozkurt.patterns.m02.examples.factorymethod.logistics.SeaLogistics;
import java.util.List;

/** Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/factorymethod/LogisticsDemo.java} */
public final class LogisticsDemo {

    private LogisticsDemo() {}

    public static void main(String[] args) {
        var cargo = new Cargo("Machine parts", 2000, 1200);
        for (Logistics logistics : List.of(new RoadLogistics(), new SeaLogistics())) {
            System.out.println(logistics.planDelivery(cargo));
        }
    }
}
