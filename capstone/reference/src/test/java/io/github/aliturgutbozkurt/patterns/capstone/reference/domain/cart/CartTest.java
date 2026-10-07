package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import org.junit.jupiter.api.Test;

class CartTest {

    private static final Sku BOOK = new Sku("BOK-001");
    private static final Sku TOY = new Sku("TOY-001");
    private final Cart empty = Cart.empty(new CartId("cart-1"), new CustomerId("alice"));

    @Test
    void addMergesIntoTheExistingLine() {
        Cart cart = empty.withAdded(BOOK, 1).withAdded(TOY, 2).withAdded(BOOK, 3);

        assertThat(cart.items()).containsExactly(new CartItem(BOOK, 4), new CartItem(TOY, 2));
        assertThat(empty.items()).as("edits never change the original").isEmpty();
    }

    @Test
    void quantityZeroRemovesAndRemovedLinesCanBeRestoredAtTheirPosition() {
        Cart cart = empty.withAdded(BOOK, 1).withAdded(TOY, 2);

        Cart withoutBook = cart.withQuantity(BOOK, 0);
        assertThat(withoutBook.items()).containsExactly(new CartItem(TOY, 2));
        assertThat(withoutBook.withItemAt(0, new CartItem(BOOK, 1))).isEqualTo(cart);
    }

    @Test
    void invalidEditsAreRejected() {
        Cart cart = empty.withAdded(BOOK, 1);

        assertThatIllegalArgumentException().isThrownBy(() -> cart.withAdded(TOY, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> cart.withQuantity(BOOK, -1));
        assertThatIllegalArgumentException().isThrownBy(() -> cart.without(TOY)).withMessage("not in cart: TOY-001");
        assertThatIllegalArgumentException().isThrownBy(() -> cart.withItemAt(0, new CartItem(BOOK, 1)));
    }

    @Test
    void closedCartRejectsEveryEdit() {
        Cart closed = empty.withAdded(BOOK, 1).closed();

        assertThat(closed.open()).isFalse();
        assertThatIllegalStateException().isThrownBy(() -> closed.withAdded(BOOK, 1)).withMessage("cart closed: cart-1");
        assertThatIllegalStateException().isThrownBy(() -> closed.withCoupon("AUTUMN5"));
        assertThatIllegalStateException().isThrownBy(closed::requireOpen);
    }
}
