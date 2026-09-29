package io.github.aliturgutbozkurt.patterns.m02.examples.singleton.testability.before;

/**
 * Hidden dependency: nothing in the constructor or method signature reveals that this class uses global state.
 *
 * @see "m02 lesson, section Singleton — why it is often an anti-pattern"
 */
public final class OrderService {

    public String placeOrder() {
        return "ORD-" + SequenceGenerator.getInstance().next();
    }
}
