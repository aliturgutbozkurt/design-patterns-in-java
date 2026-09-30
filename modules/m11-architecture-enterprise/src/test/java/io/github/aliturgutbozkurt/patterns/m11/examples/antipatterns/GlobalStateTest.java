package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after.InMemoryStockLevels;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.after.ReorderService;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderPolicy;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ServiceLocator;
import io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.StockLevels;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GlobalStateTest {

    @AfterEach
    void resetGlobalState() {
        ServiceLocator.reset(); // without this, every test below would see the previous one's stock
    }

    private static List<String> beforeScenario(Map<String, Integer> stock) {
        ServiceLocator.register(StockLevels.class, StockLevels.getInstance());
        ServiceLocator.register(ReorderPolicy.class, new ReorderPolicy(5));
        stock.forEach(StockLevels.getInstance()::set);
        return new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderService()
                .itemsToReorder();
    }

    private static List<String> afterScenario(Map<String, Integer> stock) {
        var levels = new InMemoryStockLevels();
        stock.forEach(levels::set);
        return new ReorderService(levels, () -> 5).itemsToReorder();
    }

    @Test
    void beforeLeaksStateFromOneScenarioIntoTheNextUntilReset() {
        assertThat(beforeScenario(Map.of("PEN-7", 2))).containsExactly("PEN-7");
        assertThat(beforeScenario(Map.of("MUG-3", 1))).containsExactly("MUG-3", "PEN-7"); // documents the leak
        ServiceLocator.reset();
        assertThat(beforeScenario(Map.of("MUG-3", 1))).containsExactly("MUG-3");
    }

    @Test
    void beforeFailsAtRunTimeWhenNothingIsRegistered() {
        var service = new io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderService();
        assertThatIllegalStateException().isThrownBy(service::itemsToReorder)
                .withMessage("no service registered for StockLevels");
    }

    @Test
    void beforeHasANoArgumentConstructorAlthoughItNeedsTwoCollaborators() throws NoSuchMethodException {
        assertThat(io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before.ReorderService.class
                .getConstructor().getParameterCount()).isZero();
    }

    @Test
    void afterServicesWithTheirOwnStockNeverInterfere() {
        assertThat(afterScenario(Map.of("PEN-7", 2))).containsExactly("PEN-7");
        assertThat(afterScenario(Map.of("MUG-3", 1))).containsExactly("MUG-3");
    }

    @Test
    void afterConstructorRevealsEveryDependency() {
        assertThat(ReorderService.class.getConstructors()).singleElement()
                .satisfies(c -> assertThat(c.getParameterTypes())
                        .containsExactly(io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate
                                .after.StockLevels.class, IntSupplier.class));
    }

    @Test
    void reorderDecisionsAreIdenticalForTheSameStockTable() {
        Map<String, Integer> table = Map.of("PEN-7", 4, "BOOK-1", 5, "MUG-3", 0, "TOY-2", 12);
        List<String> expected = List.of("MUG-3", "PEN-7");
        assertThat(beforeScenario(table)).isEqualTo(expected);
        assertThat(afterScenario(table)).isEqualTo(expected);
    }

    @Test
    void demoPrintsTheLeakAndTheFix() {
        assertThat(Console.capture(() -> GlobalStateDemo.main(new String[0]))).isEqualTo("""
                before, scenario 1: reorder [PEN-7]
                before, scenario 2: reorder [MUG-3, PEN-7]  <- PEN-7 leaked in
                after, scenario 1: reorder [PEN-7]
                after, scenario 2: reorder [MUG-3]
                """);
    }
}
