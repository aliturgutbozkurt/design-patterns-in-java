package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartView;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.CartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.PromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.Cart;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids.SequentialIds;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import java.time.Clock;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Features F2 and F3: carts and their edits.
 *
 * @see "capstone guide §2 Slice walkthrough — C3"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "application service behind an inbound port")
public final class CartService implements CartUseCase {

    private final CartRepository carts;
    private final ProductRepository products;
    private final PromotionRepository promotions;
    private final SequentialIds<CartId> ids;
    private final Clock clock;
    private final UnitOfWork unitOfWork;

    public CartService(CartRepository carts, ProductRepository products, PromotionRepository promotions,
                       SequentialIds<CartId> ids, Clock clock, UnitOfWork unitOfWork) {
        this.carts = Objects.requireNonNull(carts, "carts");
        this.products = Objects.requireNonNull(products, "products");
        this.promotions = Objects.requireNonNull(promotions, "promotions");
        this.ids = Objects.requireNonNull(ids, "ids");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
    }

    @Override
    public CartId open(CustomerId customer) {
        Objects.requireNonNull(customer, "customer");
        return unitOfWork.run(() -> {
            CartId id = ids.next();
            carts.save(Cart.empty(id, customer));
            return id;
        });
    }

    @Override
    public CartView add(CartId cart, Sku sku, int quantity) {
        return edit(cart, current -> {
            current.requireOpen();
            if (products.find(sku).isEmpty()) {
                throw new IllegalArgumentException("unknown product: " + sku.value());
            }
            return current.withAdded(sku, quantity);
        });
    }

    @Override
    public CartView changeQuantity(CartId cart, Sku sku, int quantity) {
        return edit(cart, current -> current.withQuantity(sku, quantity));
    }

    @Override
    public CartView remove(CartId cart, Sku sku) {
        return edit(cart, current -> current.without(sku));
    }

    @Override
    public CartView applyCoupon(CartId cart, String code) {
        return edit(cart, current -> {
            current.requireOpen();
            CouponRule coupon = promotions.coupon(code)
                    .orElseThrow(() -> new IllegalArgumentException("unknown coupon: " + code));
            if (!coupon.isValidOn(LocalDate.now(clock))) {
                throw new IllegalArgumentException("expired coupon: " + code);
            }
            return current.withCoupon(code);
        });
    }

    @Override
    public CartView view(CartId cart) {
        return Views.of(load(cart));
    }

    @Override
    public boolean undo(CartId cart) {
        load(cart);
        throw new UnsupportedOperationException("undo arrives with the Command slice (C5)");
    }

    @Override
    public boolean redo(CartId cart) {
        load(cart);
        throw new UnsupportedOperationException("redo arrives with the Command slice (C5)");
    }

    private CartView edit(CartId id, UnaryOperator<Cart> change) {
        return unitOfWork.run(() -> {
            Cart changed = change.apply(load(id));
            carts.save(changed);
            return Views.of(changed);
        });
    }

    private Cart load(CartId id) {
        return carts.find(id).orElseThrow(() -> new NoSuchElementException("unknown cart: " + id.value()));
    }
}
