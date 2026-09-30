package io.github.aliturgutbozkurt.patterns.m03.examples.prototype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Archer;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Position;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Soldier;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Stats;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.Unit;
import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.registry.UnitRegistry;
import io.github.aliturgutbozkurt.patterns.m03.support.Console;
import org.junit.jupiter.api.Test;

class UnitRegistryTest {

    static UnitRegistry registry() {
        var registry = new UnitRegistry();
        registry.register("soldier", new Soldier(new Stats(100, 12, 1)));
        registry.register("archer", new Archer(new Stats(70, 9, 6), 20));
        return registry;
    }

    @Test
    void spawnedUnitsAreIndependentOfEachOther() {
        var registry = registry();
        Unit first = registry.spawn("soldier");
        Unit second = registry.spawn("soldier");
        first.moveTo(5, 5);
        assertThat(first).isNotSameAs(second);
        assertThat(second.position()).isEqualTo(new Position(0, 0));
    }

    @Test
    void thePrototypeIsNotChangedBySpawnedUnits() {
        var registry = registry();
        registry.spawn("soldier").moveTo(9, 9);
        assertThat(registry.spawn("soldier").position()).isEqualTo(new Position(0, 0));
    }

    @Test
    void immutableStatsAreSharedNotCopied() {
        var registry = registry();
        assertThat(registry.spawn("soldier").stats()).isSameAs(registry.spawn("soldier").stats());
    }

    @Test
    void mutableArcherStateIsCopied() {
        var registry = registry();
        var archer = (Archer) registry.spawn("archer");
        archer.shoot();
        archer.shoot();
        assertThat(archer.arrows()).isEqualTo(18);
        assertThat(((Archer) registry.spawn("archer")).arrows()).isEqualTo(20);
    }

    @Test
    void registeringStoresACopy() {
        var registry = new UnitRegistry();
        var soldier = new Soldier(new Stats(100, 12, 1));
        registry.register("soldier", soldier);
        soldier.moveTo(3, 3);
        assertThat(registry.spawn("soldier").position()).isEqualTo(new Position(0, 0));
    }

    @Test
    void unknownNameListsKnownNames() {
        assertThatIllegalArgumentException().isThrownBy(() -> registry().spawn("dragon"))
                .withMessage("unknown unit: dragon (known: [archer, soldier])");
    }

    @Test
    void demoSpawnsAnArmy() {
        assertThat(Console.capture(() -> UnitRegistryDemo.main(new String[0]))).isEqualTo("""
                known units: [archer, soldier]
                soldier at Position[x=1, y=0] Stats[health=100, attack=12, range=1]
                soldier at Position[x=2, y=0] Stats[health=100, attack=12, range=1]
                archer at Position[x=3, y=0] Stats[health=70, attack=9, range=6] arrows=19
                prototype archer still has 20 arrows at Position[x=0, y=0]
                """);
    }
}
