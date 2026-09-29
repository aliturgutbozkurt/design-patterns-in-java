package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/**
 * Dynamic proxies: one handler adds behaviour to <em>any</em> interface at run time.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class Proxies {

    private Proxies() {}

    /** Logs how long each call on {@code target} took, measured with {@code nanoTicker}. */
    public static <T> T timed(Class<T> iface, T target, LongSupplier nanoTicker, Consumer<String> log) {
        return create(iface, new TimingHandler(target, nanoTicker, log));
    }

    /** Lets every call through except methods annotated {@link Mutator}. */
    public static <T> T readOnly(Class<T> iface, T target) {
        return create(iface, new ReadOnlyHandler(target));
    }

    private static <T> T create(Class<T> iface, InvocationHandler handler) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException(iface.getName() + " is not an interface");
        }
        Object proxy = Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] {iface}, handler);
        return iface.cast(proxy);
    }

    /** {@code equals}, {@code hashCode} and {@code toString} reach the handler too; answer them without the target. */
    static Object objectMethod(Object proxy, Method method, Object[] args, String description) {
        return switch (method.getName()) {
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            default -> description;                             // toString
        };
    }

    static Object requireTarget(Object target) {
        return Objects.requireNonNull(target, "target");
    }
}
