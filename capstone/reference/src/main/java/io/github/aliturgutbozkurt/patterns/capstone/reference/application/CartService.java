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
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartEdit;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.EditHistory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids.SequentialIds;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.pricing.CouponRule;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Features F2 and F3: every successful edit is a {@link CartEdit} performed through the cart's {@link EditHistory}, so
 * it can be undone and redone; rejected edits throw before anything is recorded.
 *
 * @see "capstone guide, Pattern map — Command"
 */
@PatternRole(value = DesignPattern.COMMAND, role = "client (creates the edit commands)")
public final class CartService implements CartUseCase {

    private final CartRepository carts;
    private final ProductRepository products;
    private final PromotionRepository promotions;
    private final SequentialIds<CartId> ids;
    private final Clock clock;
    private final UnitOfWork unitOfWork;
    private final Map<CartId, EditHistory> histories = new ConcurrentHashMap<>();

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
        return unitOfWork.run(_ -> {
            CartId id = ids.next();
            carts.save(Cart.empty(id, customer));
            histories.put(id, new EditHistory());
            return id;
        });
    }

    @Override
    public CartView add(CartId cart, Sku sku, int quantity) {
        return perform(cart, _ -> {
            if (products.find(sku).isEmpty()) {
                throw new IllegalArgumentException("unknown product: " + sku.value());
            }
            return new CartEdit.AddItem(sku, quantity);
        });
    }

    @Override
    public CartView changeQuantity(CartId cart, Sku sku, int quantity) {
        return perform(cart, _ -> new CartEdit.ChangeQuantity(sku, quantity));
    }

    @Override
    public CartView remove(CartId cart, Sku sku) {
        return perform(cart, _ -> new CartEdit.RemoveItem(sku));
    }

    @Override
    public CartView applyCoupon(CartId cart, String code) {
        return perform(cart, _ -> {
            CouponRule coupon = promotions.coupon(code)
                    .orElseThrow(() -> new IllegalArgumentException("unknown coupon: " + code));
            if (!coupon.isValidOn(LocalDate.now(clock))) {
                throw new IllegalArgumentException("expired coupon: " + code);
            }
            return new CartEdit.ApplyCoupon(code);
        });
    }

    @Override
    public CartView view(CartId cart) {
        return Views.of(load(cart));
    }

    @Override
    public boolean undo(CartId cart) {
        return step(cart, EditHistory::undo);
    }

    @Override
    public boolean redo(CartId cart) {
        return step(cart, EditHistory::redo);
    }

    /** Validates (in {@code command}, which may throw), then performs and records the edit. */
    private CartView perform(CartId id, Function<Cart, CartEdit> command) {
        return unitOfWork.run(_ -> {
            Cart current = load(id).requireOpen();
            Cart changed = histories.get(id).perform(current, command.apply(current));
            carts.save(changed);
            return Views.of(changed);
        });
    }

    private boolean step(CartId id, BiFunction<EditHistory, Cart, Optional<Cart>> step) {
        return unitOfWork.run(_ -> {
            Cart current = load(id).requireOpen(); // a closed cart refuses undo and redo, even with nothing to replay
            Optional<Cart> changed = step.apply(histories.get(id), current);
            changed.ifPresent(carts::save);
            return changed.isPresent();
        });
    }

    private Cart load(CartId id) {
        return carts.find(id).orElseThrow(() -> new NoSuchElementException("unknown cart: " + id.value()));
    }
}
