package io.github.aliturgutbozkurt.patterns.m11.examples.di.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A deliberately tiny reflective DI container — what Spring or Guice do underneath: look at the single public
 * constructor, resolve its parameter types recursively, cache singletons, detect cycles. Every mistake it can find is
 * found at <em>run time</em>, when {@link #get} is called; hand wiring finds the same mistakes at compile time.
 *
 * @see "m11 lesson, section Dependency Injection — how a container works"
 */
public final class MiniContainer {

    private final Map<Class<?>, Class<?>> bindings = new HashMap<>();
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Set<Class<?>> singletons = new HashSet<>();

    /** Resolves {@code type} with {@code implementation}. */
    public <T> MiniContainer bind(Class<T> type, Class<? extends T> implementation) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(implementation, "implementation");
        if (implementation.isInterface() || Modifier.isAbstract(implementation.getModifiers())) {
            throw new IllegalArgumentException(implementation.getSimpleName() + " cannot be instantiated");
        }
        bindings.put(type, implementation);
        return this;
    }

    /** Resolves {@code type} with an object the caller created (configuration values, a clock, …). */
    public <T> MiniContainer bindInstance(Class<T> type, T instance) {
        instances.put(Objects.requireNonNull(type, "type"), type.cast(Objects.requireNonNull(instance, "instance")));
        return this;
    }

    /** From now on, {@code type} is created once and the same instance is returned every time. */
    public MiniContainer singleton(Class<?> type) {
        singletons.add(Objects.requireNonNull(type, "type"));
        return this;
    }

    /** Builds (or returns the cached) object for {@code type}, with all its dependencies. */
    public <T> T get(Class<T> type) {
        return type.cast(resolve(Objects.requireNonNull(type, "type"), new ArrayList<>()));
    }

    private Object resolve(Class<?> type, List<Class<?>> path) {
        if (path.contains(type)) {
            throw new IllegalStateException("dependency cycle: " + describe(Stream.concat(path.stream(), Stream.of(type))));
        }
        Object existing = instances.get(type);
        if (existing != null) {
            return existing;
        }
        path.add(type);
        try {
            Object created = create(implementationOf(type, path), path);
            if (singletons.contains(type)) {
                instances.put(type, created);
            }
            return created;
        } finally {
            path.removeLast();
        }
    }

    private Class<?> implementationOf(Class<?> type, List<Class<?>> path) {
        Class<?> bound = bindings.get(type);
        if (bound != null) {
            return bound;
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalStateException(
                    "no binding for " + type.getSimpleName() + " (resolving " + describe(path.stream()) + ")");
        }
        return type; // a concrete class needs no binding
    }

    private Object create(Class<?> implementation, List<Class<?>> path) {
        Constructor<?>[] constructors = implementation.getConstructors();
        if (constructors.length != 1) {
            throw new IllegalStateException(implementation.getSimpleName()
                    + " must have exactly one public constructor, found " + constructors.length);
        }
        Constructor<?> constructor = constructors[0];
        Object[] arguments = new Object[constructor.getParameterCount()];
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = resolve(parameterTypes[i], path);
        }
        try {
            return constructor.newInstance(arguments);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("constructor of " + implementation.getSimpleName() + " failed", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot create " + implementation.getSimpleName(), e);
        }
    }

    private static String describe(Stream<Class<?>> types) {
        return types.map(Class::getSimpleName).collect(Collectors.joining(" -> "));
    }
}
