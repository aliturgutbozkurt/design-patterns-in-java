package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EditHistoryTest {

    private static final Sku BOOK = new Sku("BOK-001");
    private final Cart empty = Cart.empty(new CartId("cart-1"), new CustomerId("alice"));
    private final EditHistory history = new EditHistory();

    @Test
    void undoAndRedoWalkTheHistory() {
        Cart one = history.perform(empty, new CartEdit.AddItem(BOOK, 1));
        Cart two = history.perform(one, new CartEdit.AddItem(BOOK, 1));

        Cart undone = history.undo(two).orElseThrow();
        assertThat(undone).isEqualTo(one);
        assertThat(history.redo(undone)).contains(two);
        assertThat(history.redo(two)).isEmpty();
    }

    @Test
    void keepsOnlyTheLastTwentyEdits() {
        Cart cart = empty;
        for (int i = 0; i < EditHistory.DEPTH + 1; i++) {
            cart = history.perform(cart, new CartEdit.AddItem(BOOK, 1));
        }
        int undone = 0;
        for (Optional<Cart> step = history.undo(cart); step.isPresent(); step = history.undo(cart)) {
            cart = step.get();
            undone++;
        }

        assertThat(undone).isEqualTo(EditHistory.DEPTH);
        assertThat(cart.items().getFirst().quantity()).isEqualTo(1);
    }

    @Test
    void newEditClearsRedoAndFailedEditIsNotRecorded() {
        Cart one = history.perform(empty, new CartEdit.AddItem(BOOK, 1));
        Cart back = history.undo(one).orElseThrow();
        Cart other = history.perform(back, new CartEdit.ApplyCoupon("AUTUMN5"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> history.perform(other, new CartEdit.RemoveItem(new Sku("TOY-001"))));

        assertThat(history.redo(other)).isEmpty();
        assertThat(history.undo(other)).contains(back);
        assertThat(history.undo(back)).isEmpty();
    }
}
