package io.github.aliturgutbozkurt.patterns.m03.examples.prototype;

import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Archer;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Soldier;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Stats;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Unit;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.UnitRegistry;
import java.util.List;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/prototype/UnitRegistryDemo.java} */
public final class UnitRegistryDemo {

    private UnitRegistryDemo() {}

    public static void main(String[] args) {
        var registry = new UnitRegistry();
        registry.register("soldier", new Soldier(new Stats(100, 12, 1)));
        registry.register("archer", new Archer(new Stats(70, 9, 6), 20));
        System.out.println("known units: " + registry.names());

        List<Unit> army = List.of(registry.spawn("soldier"), registry.spawn("soldier"), registry.spawn("archer"));
        for (int i = 0; i < army.size(); i++) {
            army.get(i).moveTo(i + 1, 0);
        }
        ((Archer) army.getLast()).shoot();
        army.forEach(unit -> System.out.println(unit.describe()));

        var prototype = (Archer) registry.spawn("archer");
        System.out.println("prototype archer still has " + prototype.arrows() + " arrows at " + prototype.position());
    }
}
