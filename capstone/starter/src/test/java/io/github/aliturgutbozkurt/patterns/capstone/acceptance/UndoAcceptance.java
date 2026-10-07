package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.item;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartView;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import org.junit.jupiter.api.Test;

/** F3 — undo / redo of cart edits, per cart, depth 20 (brief §2.2 "Undo / redo"). */
public abstract class UndoAcceptance extends AcceptanceContract {

    private CartUseCase carts() {
        return shop().carts();
    }

    private static CartLine line(String sku, int quantity) {
        return new CartLine(sku(sku), quantity);
    }

    @Test
    void undoRevertsTheLastEdit() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 2), item("TOY-001", 1));
        CartView twoLines = carts().view(cart);

        carts().changeQuantity(cart, sku("BOK-001"), 5);
        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().view(cart)).as("change quantity undone").isEqualTo(twoLines);

        carts().add(cart, sku("BOK-001"), 3);
        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().view(cart)).as("merging add undone").isEqualTo(twoLines);

        carts().applyCoupon(cart, "AUTUMN5");
        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().view(cart)).as("coupon undone").isEqualTo(twoLines);

        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().view(cart).lines()).as("add of a new line undone").containsExactly(line("BOK-001", 2));
    }

    @Test
    void undoRestoresRemovedLineAtItsPosition() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1), item("TOY-001", 3), item("HOM-001", 2));
        CartView before = carts().view(cart);

        carts().remove(cart, sku("TOY-001"));
        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().view(cart)).isEqualTo(before);

        carts().changeQuantity(cart, sku("BOK-001"), 0);
        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().view(cart).lines())
                .containsExactly(line("BOK-001", 1), line("TOY-001", 3), line("HOM-001", 2));
    }

    @Test
    void redoReappliesTheUndoneEdit() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1));
        carts().applyCoupon(cart, "AUTUMN5");
        carts().add(cart, sku("TOY-001"), 2);
        CartView afterEdits = carts().view(cart);

        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().undo(cart)).isTrue();
        assertThat(carts().redo(cart)).isTrue();
        assertThat(carts().view(cart).coupon()).isEqualTo("AUTUMN5");
        assertThat(carts().redo(cart)).isTrue();
        assertThat(carts().view(cart)).isEqualTo(afterEdits);
        assertThat(carts().redo(cart)).isFalse();
    }

    @Test
    void newEditClearsRedo() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1), item("TOY-001", 1));
        carts().undo(cart);
        carts().add(cart, sku("HOM-001"), 1);

        assertThat(carts().redo(cart)).isFalse();
        assertThat(carts().view(cart).lines()).containsExactly(line("BOK-001", 1), line("HOM-001", 1));
    }

    @Test
    void undoAndRedoReturnFalseWhenNothingToDo() {
        CartId cart = carts().open(customer("alice"));
        CartView empty = carts().view(cart);

        assertThat(carts().undo(cart)).isFalse();
        assertThat(carts().redo(cart)).isFalse();
        assertThat(carts().view(cart)).isEqualTo(empty);
    }

    @Test
    void failedEditIsNotRecorded() {
        CartId cart = kit().cartWith("alice", item("BOK-001", 1));
        CartUseCase carts = carts();
        assertThatIllegalArgumentException().isThrownBy(() -> carts.add(cart, sku("XXX-999"), 1));
        assertThatIllegalArgumentException().isThrownBy(() -> carts.changeQuantity(cart, sku("BOK-001"), -3));
        assertThatIllegalArgumentException().isThrownBy(() -> carts.applyCoupon(cart, "NOPE"));

        assertThat(carts.undo(cart)).isTrue();
        assertThat(carts.view(cart).lines()).as("the add of BOK-001 was the last recorded edit").isEmpty();
        assertThat(carts.undo(cart)).isFalse();
    }

    @Test
    void historyIsPerCartAndKeepsTwentyEdits() {
        CartId busy = carts().open(customer("alice"));
        CartId other = kit().cartWith("bob", item("TOY-001", 1));
        for (int i = 0; i < 21; i++) {
            carts().add(busy, sku("HOM-001"), 1);
        }

        for (int i = 0; i < 20; i++) {
            assertThat(carts().undo(busy)).as("undo %d", i + 1).isTrue();
        }
        assertThat(carts().undo(busy)).as("the 21st undo").isFalse();
        assertThat(carts().view(busy).lines()).as("the oldest edit is kept").containsExactly(line("HOM-001", 1));

        assertThat(carts().undo(other)).as("the other cart's history is untouched").isTrue();
        assertThat(carts().view(other).lines()).isEmpty();
    }
}
