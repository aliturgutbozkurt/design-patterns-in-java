package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.Objects;

/**
 * A cart edit as an object. Applying it returns the changed cart <em>and its inverse edit</em>, so undo is "apply the
 * inverse" and redo is "apply the inverse of the inverse" (adapted from
 * modules/m06-…/command/spreadsheet/modern/SheetEditor.java). {@link RestoreItem} exists only as the inverse of a
 * removal: it puts a line back at its old position.
 *
 * @see "capstone guide §1 Pattern map — Command"
 */
@PatternRole(value = DesignPattern.COMMAND, role = "command (sealed records; apply returns the inverse)")
public sealed interface CartEdit {

    /** @param sku the product @param quantity units to add */
    record AddItem(Sku sku, int quantity) implements CartEdit {
        public AddItem {
            Objects.requireNonNull(sku, "sku");
        }
    }

    /** @param sku the product @param quantity the new quantity, 0 removes the line */
    record ChangeQuantity(Sku sku, int quantity) implements CartEdit {
        public ChangeQuantity {
            Objects.requireNonNull(sku, "sku");
        }
    }

    /** @param sku the product */
    record RemoveItem(Sku sku) implements CartEdit {
        public RemoveItem {
            Objects.requireNonNull(sku, "sku");
        }
    }

    /** @param code the coupon code, {@code ""} removes the coupon */
    record ApplyCoupon(String code) implements CartEdit {
        public ApplyCoupon {
            Objects.requireNonNull(code, "code");
        }
    }

    /** @param position where the line was @param item the line */
    record RestoreItem(int position, CartItem item) implements CartEdit {
        public RestoreItem {
            Objects.requireNonNull(item, "item");
        }
    }

    /**
     * The result of applying an edit.
     *
     * @param cart    the changed cart
     * @param inverse the edit that changes it back
     */
    record Applied(Cart cart, CartEdit inverse) {
        public Applied {
            Objects.requireNonNull(cart, "cart");
            Objects.requireNonNull(inverse, "inverse");
        }
    }

    /** Applies this edit to {@code cart}; invalid edits throw and change nothing. */
    default Applied applyTo(Cart cart) {
        return switch (this) {
            case AddItem(var sku, var quantity) -> {
                int position = cart.positionOf(sku);
                CartEdit inverse = position < 0 ? new RemoveItem(sku)
                        : new ChangeQuantity(sku, cart.items().get(position).quantity());
                yield new Applied(cart.withAdded(sku, quantity), inverse);
            }
            case ChangeQuantity(var sku, var quantity) -> {
                Cart changed = cart.withQuantity(sku, quantity);
                int position = cart.positionOf(sku);
                CartItem old = cart.items().get(position);
                yield new Applied(changed, quantity == 0 ? new RestoreItem(position, old)
                        : new ChangeQuantity(sku, old.quantity()));
            }
            case RemoveItem(var sku) -> {
                Cart changed = cart.without(sku);
                int position = cart.positionOf(sku);
                yield new Applied(changed, new RestoreItem(position, cart.items().get(position)));
            }
            case ApplyCoupon(var code) -> new Applied(cart.withCoupon(code), new ApplyCoupon(cart.coupon()));
            case RestoreItem(var position, var item) ->
                    new Applied(cart.withItemAt(position, item), new RemoveItem(item.sku()));
        };
    }
}
