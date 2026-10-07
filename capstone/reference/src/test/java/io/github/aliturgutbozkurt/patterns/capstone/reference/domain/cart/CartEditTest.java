package io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartEdit.AddItem;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartEdit.ApplyCoupon;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartEdit.ChangeQuantity;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartEdit.RemoveItem;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.CartEdit.RestoreItem;
import java.util.List;
import org.junit.jupiter.api.Test;

class CartEditTest {

    private static final Sku BOOK = new Sku("BOK-001");
    private static final Sku TOY = new Sku("TOY-001");
    private static final Sku MUG = new Sku("HOM-001");
    private final Cart cart = Cart.empty(new CartId("cart-1"), new CustomerId("alice"))
            .withAdded(BOOK, 1).withAdded(TOY, 3).withAdded(MUG, 2);

    @Test
    void everyEditReturnsAnInverseThatRestoresTheCartExactly() {
        for (CartEdit edit : List.of(new AddItem(BOOK, 2), new AddItem(new Sku("ELE-001"), 1),
                new ChangeQuantity(TOY, 5), new ChangeQuantity(TOY, 0), new RemoveItem(BOOK),
                new ApplyCoupon("AUTUMN5"))) {
            CartEdit.Applied applied = edit.applyTo(cart);

            assertThat(applied.inverse().applyTo(applied.cart()).cart()).as("undo of %s", edit).isEqualTo(cart);
        }
    }

    @Test
    void inversesNameTheMinimalChange() {
        assertThat(new AddItem(BOOK, 2).applyTo(cart).inverse()).isEqualTo(new ChangeQuantity(BOOK, 1));
        assertThat(new AddItem(new Sku("ELE-001"), 1).applyTo(cart).inverse()).isEqualTo(new RemoveItem(new Sku("ELE-001")));
        assertThat(new RemoveItem(TOY).applyTo(cart).inverse()).isEqualTo(new RestoreItem(1, new CartItem(TOY, 3)));
        assertThat(new ApplyCoupon("AUTUMN5").applyTo(cart).inverse()).isEqualTo(new ApplyCoupon(""));
    }

    @Test
    void invalidEditThrowsWithoutAnInverse() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RemoveItem(new Sku("ELE-001")).applyTo(cart));
    }
}
