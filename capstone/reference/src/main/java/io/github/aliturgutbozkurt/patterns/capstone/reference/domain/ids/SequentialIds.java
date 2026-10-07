package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongFunction;

/**
 * A thread-safe sequence of ids ({@code cart-1}, {@code cart-2}, …) for one shop. The constructor is private: the
 * named static factories say which sequence you get and hide how its ids are formatted.
 *
 * @param <T> the id type
 * @see "capstone guide, Pattern map — Static Factory Method"
 */
@PatternRole(value = DesignPattern.STATIC_FACTORY_METHOD, role = "named constructors forCarts() and forOrders()")
public final class SequentialIds<T> {

    private final AtomicLong last = new AtomicLong();
    private final LongFunction<T> format;

    private SequentialIds(LongFunction<T> format) {
        this.format = format;
    }

    /** {@code cart-1}, {@code cart-2}, … */
    public static SequentialIds<CartId> forCarts() {
        return new SequentialIds<>(n -> new CartId("cart-" + n));
    }

    /** {@code order-1}, {@code order-2}, … — take one only when an order is really placed. */
    public static SequentialIds<OrderId> forOrders() {
        return new SequentialIds<>(OrderId::of);
    }

    /** The next id of the sequence. */
    public T next() {
        return format.apply(last.incrementAndGet());
    }
}
