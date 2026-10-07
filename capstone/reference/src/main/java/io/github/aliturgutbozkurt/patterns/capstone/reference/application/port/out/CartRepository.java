package io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.Cart;
import java.util.Optional;

/**
 * Outbound port: where carts live. Implementations are thread-safe.
 *
 * @see "capstone guide, Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.REPOSITORY, role = "repository (outbound port)")
public interface CartRepository {

    /** Inserts or replaces the cart with the same id. */
    void save(Cart cart);

    /** The cart with this id, if any. */
    Optional<Cart> find(CartId id);
}
