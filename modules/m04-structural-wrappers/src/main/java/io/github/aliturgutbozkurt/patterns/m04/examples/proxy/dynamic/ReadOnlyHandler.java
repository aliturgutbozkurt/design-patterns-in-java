package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invocation handler that blocks every method annotated {@link Mutator} — a generic protection proxy.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class ReadOnlyHandler implements InvocationHandler {

    private final Object target;

    public ReadOnlyHandler(Object target) {
        this.target = Proxies.requireTarget(target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return Proxies.objectMethod(proxy, method, args, "read-only " + target);
        }
        if (method.isAnnotationPresent(Mutator.class)) {
            throw new UnsupportedOperationException(
                    method.getDeclaringClass().getSimpleName() + "." + method.getName() + " is read-only");
        }
        try {
            return method.isDefault()
                    ? InvocationHandler.invokeDefault(proxy, method, args)
                    : method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
