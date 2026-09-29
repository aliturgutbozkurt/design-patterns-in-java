package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/**
 * Invocation handler that measures every interface call and logs {@code "Type.method took N ms"}.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class TimingHandler implements InvocationHandler {

    private final Object target;
    private final LongSupplier nanoTicker;
    private final Consumer<String> log;

    public TimingHandler(Object target, LongSupplier nanoTicker, Consumer<String> log) {
        this.target = Proxies.requireTarget(target);
        this.nanoTicker = Objects.requireNonNull(nanoTicker, "nanoTicker");
        this.log = Objects.requireNonNull(log, "log");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return Proxies.objectMethod(proxy, method, args, "timed " + target);   // not timed
        }
        long start = nanoTicker.getAsLong();
        try {
            return method.isDefault()
                    ? InvocationHandler.invokeDefault(proxy, method, args)   // its inner calls come back here
                    : method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();                                 // the target's own exception, unchanged
        } finally {
            long millis = (nanoTicker.getAsLong() - start) / 1_000_000;
            log.accept(method.getDeclaringClass().getSimpleName() + "." + method.getName() + " took " + millis + " ms");
        }
    }
}
