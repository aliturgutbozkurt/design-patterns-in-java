package io.github.aliturgutbozkurt.patterns.m11.examples.di;

import io.github.aliturgutbozkurt.patterns.m11.examples.di.styles.ConstructorInjected;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.styles.HiddenDependencies;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.styles.SetterInjected;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/** Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/di/InjectionStylesDemo.java} */
public final class InjectionStylesDemo {

    private InjectionStylesDemo() {}

    public static void main(String[] args) {
        Clock september = Clock.fixed(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC);

        var constructorInjected = new ConstructorInjected(september, new AtomicLong()::incrementAndGet);
        System.out.println("constructor injection: " + constructorInjected.next() + ", " + constructorInjected.next());
        System.out.println("  constructor parameters: " + parameters(ConstructorInjected.class));

        var setterInjected = new SetterInjected();
        try {
            setterInjected.next();
        } catch (IllegalStateException e) {
            System.out.println("setter injection before configuration: " + e.getMessage());
        }
        setterInjected.setClock(september);
        setterInjected.setSequence(new AtomicLong()::incrementAndGet);
        System.out.println("setter injection after both setters: " + setterInjected.next());

        System.out.println("hidden dependencies: constructor parameters: " + parameters(HiddenDependencies.class));
        System.out.println("  the system clock and the counter are created inside: a test cannot replace them");
    }

    private static String parameters(Class<?> type) {
        return Arrays.stream(type.getConstructors()[0].getParameterTypes()).map(Class::getSimpleName).toList().toString();
    }
}
