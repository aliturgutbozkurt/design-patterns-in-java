package io.github.aliturgutbozkurt.patterns.capstone.api.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.NoSuchElementException;

/**
 * GIVEN — do not modify. Inbound port of features F2 (cart) and F3 (undo / redo).
 *
 * <p>Every method that takes a {@link CartId} throws {@link NoSuchElementException} ({@code unknown cart: cart-9}) for
 * an unknown cart. Edits, undo and redo of a closed cart (after a successful checkout) throw
 * {@link IllegalStateException} ({@code cart closed: cart-1}). A rejected edit throws
 * {@link IllegalArgumentException}, leaves the cart unchanged and is not recorded in the history.
 *
 * @see "capstone brief, Business rules — Cart (F2), Undo / redo (F3)"
 */
public interface CartUseCase {

    /** Opens an empty cart; ids are {@code cart-1}, {@code cart-2}, … per shop. */
    CartId open(CustomerId customer);

    /**
     * Adds {@code quantity} (≥ 1) units; an existing line keeps its position and its quantity grows. An unknown product
     * is rejected ({@code unknown product: XXX-999}). Stock is not checked here.
     */
    CartView add(CartId cart, Sku sku, int quantity);

    /** Sets the quantity of a line in the cart; 0 removes the line, a negative quantity or a SKU not in the cart is rejected. */
    CartView changeQuantity(CartId cart, Sku sku, int quantity);

    /** Removes the line of {@code sku}; a SKU not in the cart is rejected. */
    CartView remove(CartId cart, Sku sku);

    /**
     * Applies a coupon promotion by its code, replacing any coupon applied before. An unknown code
     * ({@code unknown coupon: NOPE}) or a coupon whose {@code validUntil} is before today ({@code expired coupon:
     * SUMMER10}) is rejected.
     */
    CartView applyCoupon(CartId cart, String code);

    /** The current state of the cart (open or closed). */
    CartView view(CartId cart);

    /** Reverts the last recorded edit exactly (positions included); {@code false} and no change when there is none. */
    boolean undo(CartId cart);

    /** Re-applies the last undone edit; {@code false} and no change when there is none. A new edit clears redo. */
    boolean redo(CartId cart);
}
