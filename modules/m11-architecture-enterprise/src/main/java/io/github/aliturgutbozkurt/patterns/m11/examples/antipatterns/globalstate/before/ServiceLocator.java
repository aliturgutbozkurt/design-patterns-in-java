package io.github.aliturgutbozkurt.patterns.m11.examples.antipatterns.globalstate.before;

import java.util.HashMap;
import java.util.Map;

/**
 * ANTI-PATTERN — see lesson: a static registry any class can reach into. Dependencies become invisible and the
 * registry's state outlives every test unless someone remembers {@link #reset()}.
 *
 * @see "m11 lesson, section Anti-patterns — Singleton and Service Locator abuse"
 */
public final class ServiceLocator {

    private static Map<Class<?>, Object> services = new HashMap<>(); // ANTI-PATTERN: mutable static state

    private ServiceLocator() {}

    public static <T> void register(Class<T> type, T service) {
        services.put(type, type.cast(service));
    }

    public static <T> T get(Class<T> type) {
        Object service = services.get(type);
        if (service == null) {
            throw new IllegalStateException("no service registered for " + type.getSimpleName());
        }
        return type.cast(service);
    }

    /** The test-only escape hatch every global registry grows: forget everything, including the Singleton. */
    public static void reset() {
        services = new HashMap<>();
        StockLevels.resetInstance();
    }
}
