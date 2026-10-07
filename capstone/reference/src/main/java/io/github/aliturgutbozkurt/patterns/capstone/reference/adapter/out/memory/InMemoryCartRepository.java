package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.CartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.Cart;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carts in a concurrent map; carts are immutable values.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "in-memory repository (outbound adapter)")
public final class InMemoryCartRepository implements CartRepository {

    private final Map<CartId, Cart> carts = new ConcurrentHashMap<>();

    @Override
    public void save(Cart cart) {
        carts.put(Objects.requireNonNull(cart, "cart").id(), cart);
    }

    @Override
    public Optional<Cart> find(CartId id) {
        return Optional.ofNullable(carts.get(id));
    }
}
