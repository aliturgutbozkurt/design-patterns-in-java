package io.github.aliturgutbozkurt.patterns.m11.examples.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.github.aliturgutbozkurt.patterns.m11.examples.di.styles.ConstructorInjected;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.styles.HiddenDependencies;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.styles.SetterInjected;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class InjectionStylesTest {

    private static final Clock SEPTEMBER = Clock.fixed(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void constructorInjectedRejectsNullAtConstruction() {
        assertThatNullPointerException().isThrownBy(() -> new ConstructorInjected(null, () -> 1L)).withMessage("clock");
        assertThatNullPointerException().isThrownBy(() -> new ConstructorInjected(SEPTEMBER, null))
                .withMessage("sequence");
    }

    @Test
    void constructorInjectedWithFixedClockAndSequenceIsPredictable() {
        var numberer = new ConstructorInjected(SEPTEMBER, new AtomicLong()::incrementAndGet);
        assertThat(numberer.next()).isEqualTo("INV-2026-09-0001");
        assertThat(numberer.next()).isEqualTo("INV-2026-09-0002");
    }

    @Test
    void constructorInjectedDeclaresEveryDependency() {
        assertThat(parameterTypes(ConstructorInjected.class)).containsExactly(Clock.class, Supplier.class);
    }

    @Test
    void setterInjectedUsedBeforeBothSettersThrows() {
        var numberer = new SetterInjected();
        assertThatIllegalStateException().isThrownBy(numberer::next).withMessage("clock not set");
        numberer.setClock(SEPTEMBER);
        assertThatIllegalStateException().isThrownBy(numberer::next).withMessage("sequence not set");
    }

    @Test
    void setterInjectedWorksOnceFullyConfigured() {
        var numberer = new SetterInjected();
        numberer.setClock(SEPTEMBER);
        numberer.setSequence(new AtomicLong(41)::incrementAndGet);
        assertThat(numberer.next()).isEqualTo("INV-2026-09-0042");
    }

    @Test
    void hiddenDependenciesAreInvisibleInTheConstructor() {
        assertThat(HiddenDependencies.class.getConstructors())
                .allSatisfy(constructor -> assertThat(constructor.getParameterCount()).isZero());
    }

    @Test
    void hiddenDependenciesStillWorkButOnlyWithTheRealClock() {
        // The only thing a test can assert is the shape: the date part depends on today's system clock.
        assertThat(new HiddenDependencies().next()).matches("INV-\\d{4}-\\d{2}-0001");
    }

    @Test
    void demoPrintsTheThreeStyles() {
        assertThat(Console.capture(() -> InjectionStylesDemo.main(new String[0]))).isEqualTo("""
                constructor injection: INV-2026-09-0001, INV-2026-09-0002
                  constructor parameters: [Clock, Supplier]
                setter injection before configuration: clock not set
                setter injection after both setters: INV-2026-09-0001
                hidden dependencies: constructor parameters: []
                  the system clock and the counter are created inside: a test cannot replace them
                """);
    }

    private static Class<?>[] parameterTypes(Class<?> type) {
        Constructor<?>[] constructors = type.getConstructors();
        assertThat(constructors).hasSize(1);
        return constructors[0].getParameterTypes();
    }
}
