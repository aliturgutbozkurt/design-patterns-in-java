package io.github.aliturgutbozkurt.patterns.m09.examples.immutability.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefensiveCopyTest {

    private static final CartLine MUG = new CartLine("MUG-0001", 2, 12_50);
    private static final CartLine TEE = new CartLine("TEE-0002", 1, 20_00);
    private static final CartLine CAP = new CartLine("CAP-0003", 1, 8_00);

    @Test
    void changingTheSourceListChangesLeakyCartButNotCart() {
        var source = new ArrayList<>(List.of(MUG));
        var leaky = new LeakyCart(source);
        var cart = new Cart(source);
        source.add(TEE);
        assertThat(leaky.lines()).containsExactly(MUG, TEE);
        assertThat(cart.lines()).containsExactly(MUG);
    }

    @Test
    void leakyCartReturnsTheCallersOwnListObject() {
        var source = new ArrayList<>(List.of(MUG));
        assertThat(new LeakyCart(source).lines()).isSameAs(source);
        assertThat(new Cart(source).lines()).isNotSameAs(source);
    }

    @Test
    void cartLinesCannotBeModified() {
        var cart = new Cart(List.of(MUG));
        assertThatThrownBy(() -> cart.lines().add(TEE)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void aNullLineIsRejectedByListCopyOf() {
        assertThatNullPointerException().isThrownBy(() -> new Cart(Arrays.asList(MUG, null)));
    }

    @Test
    void everyWitherReturnsANewCartAndLeavesTheOriginalUnchanged() {
        var original = new Cart(List.of(MUG, TEE));
        var added = original.withLine(CAP);
        var removed = original.withoutSku("MUG-0001");
        var changed = original.withQuantity("TEE-0002", 3);
        assertThat(original.lines()).containsExactly(MUG, TEE);
        assertThat(added.lines()).containsExactly(MUG, TEE, CAP);
        assertThat(removed.lines()).containsExactly(TEE);
        assertThat(changed.lines()).containsExactly(MUG, new CartLine("TEE-0002", 3, 20_00));
        assertThat(List.of(added, removed, changed)).allSatisfy(c -> assertThat(c).isNotSameAs(original));
    }

    @Test
    void withQuantityZeroRemovesTheLine() {
        assertThat(new Cart(List.of(MUG, TEE)).withQuantity("MUG-0001", 0).lines()).containsExactly(TEE);
        assertThatIllegalArgumentException().isThrownBy(() -> new Cart(List.of(MUG)).withQuantity("MUG-0001", -1))
                .withMessage("quantity must not be negative: -1");
    }

    @Test
    void totalIsComputedFromTheLines() {
        assertThat(new Cart(List.of(MUG, TEE)).totalCents()).isEqualTo(45_00);
        assertThat(new Cart(List.of()).totalCents()).isZero();
    }

    @Test
    void anUnmodifiableViewShowsLaterChangesButACopyDoesNot() {
        var source = new ArrayList<>(List.of("a"));
        List<String> view = Collections.unmodifiableList(source);
        List<String> copy = List.copyOf(source);
        source.add("b");
        assertThat(view).containsExactly("a", "b");
        assertThat(copy).containsExactly("a");
    }

    @Test
    void demoPrintsTheLeakTheFixAndTheWithers() {
        assertThat(Console.capture(() -> DefensiveCopyDemo.main(new String[0]))).isEqualTo("""
                -- a record is only as immutable as its components
                after source.add(TEE): LeakyCart has 2 line(s), Cart has 1 line(s)
                leaky.lines() == source: true
                cart.lines().add(...) -> UnsupportedOperationException
                -- change = a new value (withers)
                original       [MUG-0001 x2, TEE-0002 x1] total 4500
                withLine(CAP)  [MUG-0001 x2, TEE-0002 x1, CAP-0003 x1] total 5300
                withoutSku(MUG)[TEE-0002 x1] total 2000
                withQuantity 3 [MUG-0001 x2, TEE-0002 x3] total 8500
                original again [MUG-0001 x2, TEE-0002 x1] total 4500
                -- view vs. copy
                unmodifiableList view: [a, b]
                List.copyOf copy:      [a]
                """);
    }
}
