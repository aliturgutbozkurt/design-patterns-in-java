package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * The undo and redo stacks of one cart: performs edits, keeps the inverses of the last {@value #DEPTH} and replays
 * them. Not thread-safe; the cart service changes it only inside a transaction.
 *
 * @see "capstone guide §1 Pattern map — Command"
 */
@PatternRole(value = DesignPattern.COMMAND, role = "invoker (undo/redo history)")
public final class EditHistory {

    /** How many edits can be undone. */
    public static final int DEPTH = 20;

    private final Deque<CartEdit> undo = new ArrayDeque<>();
    private final Deque<CartEdit> redo = new ArrayDeque<>();

    /** Applies {@code edit}, remembers its inverse and clears the redo history. */
    public Cart perform(Cart cart, CartEdit edit) {
        CartEdit.Applied applied = edit.applyTo(cart);
        undo.push(applied.inverse());
        if (undo.size() > DEPTH) {
            undo.removeLast();
        }
        redo.clear();
        return applied.cart();
    }

    /** The cart with the last edit undone, or empty if there is nothing to undo. */
    public Optional<Cart> undo(Cart cart) {
        return replay(cart, undo, redo);
    }

    /** The cart with the last undone edit redone, or empty if there is nothing to redo. */
    public Optional<Cart> redo(Cart cart) {
        return replay(cart, redo, undo);
    }

    private static Optional<Cart> replay(Cart cart, Deque<CartEdit> from, Deque<CartEdit> to) {
        CartEdit edit = from.peek();
        if (edit == null) {
            return Optional.empty();
        }
        CartEdit.Applied applied = edit.applyTo(cart);
        from.pop();
        to.push(applied.inverse());
        return Optional.of(applied.cart());
    }
}
