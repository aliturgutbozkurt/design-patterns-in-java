package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.customer;
import static io.github.aliturgutbozkurt.patterns.capstone.acceptance.ShopTestKit.sku;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartView;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CartId;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PromotionSpec;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/** F2 — cart: open, add (merge), change quantity, remove, coupon (brief §2.2 "Cart"). */
public abstract class CartAcceptance extends AcceptanceContract {

    private CartUseCase carts() {
        return shop().carts();
    }

    private static CartLine line(String sku, int quantity) {
        return new CartLine(sku(sku), quantity);
    }

    @Test
    void newCartIsEmptyAndOpen() {
        CartId first = carts().open(customer("alice"));
        CartId second = carts().open(customer("bob"));

        assertThat(first).isEqualTo(new CartId("cart-1"));
        assertThat(second).isEqualTo(new CartId("cart-2"));
        assertThat(carts().view(first)).isEqualTo(new CartView(first, customer("alice"), List.of(), "", true));
        assertThat(carts().view(second)).isEqualTo(new CartView(second, customer("bob"), List.of(), "", true));
    }

    @Test
    void addingSameSkuMergesQuantitiesKeepingFirstPosition() {
        CartId cart = carts().open(customer("alice"));
        carts().add(cart, sku("BOK-001"), 1);
        carts().add(cart, sku("TOY-001"), 2);
        CartView view = carts().add(cart, sku("BOK-001"), 3);

        assertThat(view.lines()).containsExactly(line("BOK-001", 4), line("TOY-001", 2));
        assertThat(carts().view(cart)).isEqualTo(view);
    }

    @Test
    void changeQuantityToZeroRemovesTheLine() {
        CartId cart = kit().cartWith("alice", ShopTestKit.item("BOK-001", 1), ShopTestKit.item("TOY-001", 2),
                ShopTestKit.item("HOM-001", 1));

        assertThat(carts().changeQuantity(cart, sku("TOY-001"), 5).lines())
                .containsExactly(line("BOK-001", 1), line("TOY-001", 5), line("HOM-001", 1));
        assertThat(carts().changeQuantity(cart, sku("TOY-001"), 0).lines())
                .containsExactly(line("BOK-001", 1), line("HOM-001", 1));
        assertThat(carts().remove(cart, sku("BOK-001")).lines()).containsExactly(line("HOM-001", 1));
        assertThat(carts().view(cart).lines()).containsExactly(line("HOM-001", 1));
    }

    @Test
    void rejectsUnknownProductAndNonPositiveQuantityLeavingCartUnchanged() {
        CartId cart = kit().cartWith("alice", ShopTestKit.item("BOK-001", 2));
        CartUseCase carts = carts();

        assertThatIllegalArgumentException().isThrownBy(() -> carts.add(cart, sku("XXX-999"), 1))
                .withMessage("unknown product: XXX-999");
        assertThatIllegalArgumentException().as("zero").isThrownBy(() -> carts.add(cart, sku("BOK-001"), 0));
        assertThatIllegalArgumentException().as("negative").isThrownBy(() -> carts.add(cart, sku("TOY-001"), -1));
        assertThatIllegalArgumentException().as("negative change")
                .isThrownBy(() -> carts.changeQuantity(cart, sku("BOK-001"), -1));
        assertThatIllegalArgumentException().as("change of a SKU not in the cart")
                .isThrownBy(() -> carts.changeQuantity(cart, sku("TOY-001"), 1));
        assertThatIllegalArgumentException().as("removal of a SKU not in the cart")
                .isThrownBy(() -> carts.remove(cart, sku("TOY-001")));
        assertThat(carts.view(cart).lines()).containsExactly(line("BOK-001", 2));

        assertThat(carts.add(cart, sku("TOY-001"), 50).lines()).as("stock is not checked while shopping")
                .containsExactly(line("BOK-001", 2), line("TOY-001", 50));
    }

    @Test
    void unknownCartIsReported() {
        CartUseCase carts = carts();
        CartId unknown = new CartId("cart-9");

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> carts.view(unknown))
                .withMessage("unknown cart: cart-9");
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> carts.add(unknown, sku("BOK-001"), 1))
                .withMessage("unknown cart: cart-9");
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> carts.undo(unknown))
                .withMessage("unknown cart: cart-9");
    }

    @Test
    void couponIsValidatedWhenApplied() {
        shop().pricing().addPromotion(new PromotionSpec.Coupon("SUMMER10", 10, LocalDate.of(2026, 8, 31)));
        shop().pricing().addPromotion(new PromotionSpec.Coupon("WINTER10", 10, LocalDate.of(2027, 2, 28)));
        shop().pricing().addPromotion(new PromotionSpec.Coupon("TODAY1", 1, LocalDate.of(2026, 11, 16)));
        CartId cart = kit().cartWith("alice", ShopTestKit.item("BOK-001", 1));
        CartUseCase carts = carts();

        assertThatIllegalArgumentException().isThrownBy(() -> carts.applyCoupon(cart, "NOPE"))
                .withMessage("unknown coupon: NOPE");
        assertThatIllegalArgumentException().isThrownBy(() -> carts.applyCoupon(cart, "SUMMER10"))
                .withMessage("expired coupon: SUMMER10");
        assertThat(carts.view(cart).coupon()).isEmpty();

        assertThat(carts.applyCoupon(cart, "AUTUMN5").coupon()).isEqualTo("AUTUMN5");
        assertThat(carts.applyCoupon(cart, "WINTER10").coupon()).as("a second valid coupon replaces the first")
                .isEqualTo("WINTER10");
        assertThat(carts.applyCoupon(cart, "TODAY1").coupon()).as("valid on its last day").isEqualTo("TODAY1");
        assertThat(carts.view(cart).coupon()).isEqualTo("TODAY1");
    }
}
