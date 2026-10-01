package io.github.aliturgutbozkurt.patterns.m09.examples.composition.text;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m09.support.Console;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SlugifierTest {

    private static final Locale TR = Locale.forLanguageTag("tr");

    @Test
    void turkishTitleBecomesAnAsciiSlug() {
        assertThat(Slugifier.forLocale(TR).apply("İstanbul'da Kış İndirimi")).isEqualTo("istanbulda-kis-indirimi");
    }

    @Test
    void turkishLowerCaseOfCapitalIIsTheDotlessI() {
        assertThat("ISTANBUL".toLowerCase(TR)).isEqualTo("ıstanbul");
        assertThat("i".toUpperCase(TR)).isEqualTo("İ");
        assertThat(Slugifier.transliterateTurkish().apply("ıstanbul")).isEqualTo("istanbul");
        assertThat(Slugifier.forLocale(TR).apply("ISTANBUL")).isEqualTo("istanbul");
    }

    @Test
    void nfdStrippingAloneLeavesTheDotlessIUnchanged() {
        assertThat(Slugifier.stripDiacritics().apply("şöğüç")).isEqualTo("soguc");
        assertThat(Slugifier.stripDiacritics().apply("ı")).isEqualTo("ı");
        assertThat(Slugifier.withoutTransliteration(TR).apply("ISTANBUL")).isEqualTo("stanbul");
    }

    @Test
    void capitalDottedIInTheRootLocaleLowerCasesToTwoChars() {
        assertThat("İ".toLowerCase(Locale.ROOT)).hasSize(2).isEqualTo("i̇");
        assertThat("İ".toLowerCase(TR)).isEqualTo("i");
    }

    @Test
    void eachStepWorksOnItsOwn() {
        assertThat(Slugifier.lowerCase(Locale.ROOT).apply("Mug XL")).isEqualTo("mug xl");
        assertThat(Slugifier.transliterateTurkish().apply("Iİıi")).isEqualTo("IIii");
        assertThat(Slugifier.stripDiacritics().apply("café")).isEqualTo("cafe");
        assertThat(Slugifier.replaceNonAlphanumeric().apply("a b'c!d")).isEqualTo("a-bc-d");
        assertThat(Slugifier.collapseDashes().apply("a---b--c")).isEqualTo("a-b-c");
        assertThat(Slugifier.trimDashes().apply("--a-b--")).isEqualTo("a-b");
    }

    @Test
    void composeAppliesItsArgumentFirst() {
        var viaCompose = Slugifier.trimDashes().compose(Slugifier.collapseDashes());
        var viaAndThen = Slugifier.collapseDashes().andThen(Slugifier.trimDashes());
        assertThat(viaCompose.apply("--a--b--")).isEqualTo("a-b").isEqualTo(viaAndThen.apply("--a--b--"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  --Merhaba Dünya!-- ", "!!!Çay & Simit???", "'Ölçü'", "x", "---"})
    void slugNeverStartsOrEndsWithADash(String title) {
        assertThat(Slugifier.forLocale(TR).apply(title)).doesNotStartWith("-").doesNotEndWith("-")
                .matches("[a-z0-9-]*");
    }

    @Test
    void demoPrintsSlugsAndTheTurkishIPitfall() {
        assertThat(Console.capture(() -> SlugifierDemo.main(new String[0]))).isEqualTo("""
                -- a slug pipeline of six small functions
                "İstanbul'da Kış İndirimi" -> istanbulda-kis-indirimi
                "Çay & Simit Seti (2 kişilik)" -> cay-simit-seti-2-kisilik
                "ŞEKER BAYRAMI" -> seker-bayrami
                -- why every case conversion names its Locale
                "ISTANBUL".toLowerCase(tr)   = ıstanbul
                "ISTANBUL".toLowerCase(ROOT) = istanbul
                "İ".toLowerCase(ROOT).length() = 2
                without the transliteration step: "ISTANBUL" -> stanbul
                -- compose vs. andThen
                trimDashes.compose(collapseDashes)("--a--b--") = a-b
                """);
    }
}
