package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The cart aggregate as an immutable value: every edit returns a new cart, which makes undo a matter of keeping the
 * right edits (brief §2.2 "Cart"). Edits of a closed cart throw {@link IllegalStateException}.
 *
 * @param id       the cart
 * @param customer its owner
 * @param items    lines in the order they were first added
 * @param coupon   the applied coupon code, {@code ""} when none
 * @param open     {@code false} after checkout
 * @see "capstone guide §2 Slice walkthrough — C3"
 */
public record Cart(CartId id, CustomerId customer, List<CartItem> items, String coupon, boolean open) {

    public Cart {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        items = List.copyOf(items);
        Objects.requireNonNull(coupon, "coupon");
    }

    /** A new, empty, open cart. */
    public static Cart empty(CartId id, CustomerId customer) {
        return new Cart(id, customer, List.of(), "", true);
    }

    /** This cart, if it is open; otherwise {@code IllegalStateException("cart closed: cart-1")}. */
    public Cart requireOpen() {
        if (!open) {
            throw new IllegalStateException("cart closed: " + id.value());
        }
        return this;
    }

    /** Adds units; an existing line keeps its position and grows. */
    public Cart withAdded(Sku sku, int quantity) {
        requireOpen();
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be positive: " + quantity);
        }
        int position = positionOf(sku);
        if (position < 0) {
            return withItemAt(items.size(), new CartItem(sku, quantity));
        }
        return withQuantity(sku, Math.addExact(items.get(position).quantity(), quantity));
    }

    /** Sets a line's quantity; 0 removes it. */
    public Cart withQuantity(Sku sku, int quantity) {
        requireOpen();
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative: " + quantity);
        }
        int position = requirePosition(sku);
        if (quantity == 0) {
            return without(sku);
        }
        List<CartItem> changed = new ArrayList<>(items);
        changed.set(position, new CartItem(sku, quantity));
        return new Cart(id, customer, changed, coupon, open);
    }

    /** Removes a line. */
    public Cart without(Sku sku) {
        requireOpen();
        List<CartItem> changed = new ArrayList<>(items);
        changed.remove(requirePosition(sku));
        return new Cart(id, customer, changed, coupon, open);
    }

    /** Inserts a line at {@code position} (0 … size) — how undo puts a removed line back where it was. */
    public Cart withItemAt(int position, CartItem item) {
        requireOpen();
        if (positionOf(item.sku()) >= 0) {
            throw new IllegalArgumentException("already in cart: " + item.sku().value());
        }
        List<CartItem> changed = new ArrayList<>(items);
        changed.add(position, item);
        return new Cart(id, customer, changed, coupon, open);
    }

    /** Applies a coupon code ({@code ""} removes the coupon); the code is validated by the caller. */
    public Cart withCoupon(String code) {
        requireOpen();
        return new Cart(id, customer, items, Objects.requireNonNull(code, "code"), open);
    }

    /** This cart, closed for good (after a successful checkout). */
    public Cart closed() {
        return new Cart(id, customer, items, coupon, false);
    }

    /** The position of {@code sku}'s line, or -1. */
    public int positionOf(Sku sku) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).sku().equals(sku)) {
                return i;
            }
        }
        return -1;
    }

    private int requirePosition(Sku sku) {
        int position = positionOf(sku);
        if (position < 0) {
            throw new IllegalArgumentException("not in cart: " + sku.value());
        }
        return position;
    }
}
