package io.github.aliturgutbozkurt.patterns.m02.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

/** Assignment 01 — colour values with static factories. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract Color rgb(int red, int green, int blue);

    protected abstract Color hex(String text);

    protected abstract Color named(String name);

    /** The student's (or solution's) colour class, for the constructor check. */
    protected abstract Class<? extends Color> colorClass();

    private static void assertComponents(Color color, int red, int green, int blue) {
        assertThat(color.red()).isEqualTo(red);
        assertThat(color.green()).isEqualTo(green);
        assertThat(color.blue()).isEqualTo(blue);
    }

    @Test
    void rgbRejectsComponentsOutsideZeroTo255() {
        assertThatIllegalArgumentException().isThrownBy(() -> rgb(256, 0, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> rgb(0, -1, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> rgb(0, 0, 300));
        assertComponents(rgb(0, 128, 255), 0, 128, 255);
    }

    @Test
    void hexParsesLongForm() {
        assertComponents(hex("#FF8800"), 255, 136, 0);
    }

    @Test
    void hexParsesShortForm() {
        assertComponents(hex("#F80"), 255, 136, 0);
    }

    @Test
    void hexIsCaseInsensitive() {
        assertThat(hex("#ff8800")).isEqualTo(hex("#FF8800"));
    }

    @Test
    void hexRejectsMalformedInput() {
        assertThatIllegalArgumentException().isThrownBy(() -> hex("FF8800"));
        assertThatIllegalArgumentException().isThrownBy(() -> hex("#GG0000"));
        assertThatIllegalArgumentException().isThrownBy(() -> hex("#12345"));
        assertThatIllegalArgumentException().isThrownBy(() -> hex(""));
    }

    @Test
    void namedColoursAreCached() {
        assertThat(named("red")).isSameAs(named("red")).isSameAs(named("RED"));
        assertComponents(named("magenta"), 255, 0, 255);
        assertComponents(named("black"), 0, 0, 0);
    }

    @Test
    void rgbReturnsCachedInstanceForNamedColour() {
        assertThat(rgb(255, 0, 0)).isSameAs(named("red"));
        assertThat(rgb(255, 255, 255)).isSameAs(named("white"));
    }

    @Test
    void unknownNameListsKnownNames() {
        assertThatIllegalArgumentException().isThrownBy(() -> named("orange"))
                .withMessageContaining("orange")
                .withMessageContaining("black")
                .withMessageContaining("yellow");
    }

    @Test
    void toHexIsUppercaseSixDigits() {
        assertThat(rgb(10, 11, 12).toHex()).isEqualTo("#0A0B0C");
        assertThat(hex("#f80").toHex()).isEqualTo("#FF8800");
    }

    @Test
    void equalByValue() {
        assertThat(rgb(1, 2, 3)).isEqualTo(rgb(1, 2, 3)).hasSameHashCodeAs(rgb(1, 2, 3));
        assertThat(rgb(1, 2, 3)).isNotEqualTo(rgb(1, 2, 4));
    }

    @Test
    void hasNoPublicConstructor() {
        assertThat(colorClass().getConstructors()).isEmpty();
    }
}
