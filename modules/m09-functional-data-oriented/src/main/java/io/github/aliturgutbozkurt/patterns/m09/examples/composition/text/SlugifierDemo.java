package io.github.aliturgutbozkurt.patterns.m09.examples.composition.text;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/composition/text/SlugifierDemo.java} */
public final class SlugifierDemo {

    private SlugifierDemo() {}

    public static void main(String[] args) {
        Locale tr = Locale.forLanguageTag("tr");
        Function<String, String> slug = Slugifier.forLocale(tr);

        System.out.println("-- a slug pipeline of six small functions");
        for (String title : List.of("İstanbul'da Kış İndirimi", "Çay & Simit Seti (2 kişilik)", "ŞEKER BAYRAMI")) {
            System.out.println("\"" + title + "\" -> " + slug.apply(title));
        }

        System.out.println("-- why every case conversion names its Locale");
        System.out.println("\"ISTANBUL\".toLowerCase(tr)   = " + "ISTANBUL".toLowerCase(tr));
        System.out.println("\"ISTANBUL\".toLowerCase(ROOT) = " + "ISTANBUL".toLowerCase(Locale.ROOT));
        System.out.println("\"İ\".toLowerCase(ROOT).length() = " + "İ".toLowerCase(Locale.ROOT).length());
        System.out.println("without the transliteration step: \"ISTANBUL\" -> "
                + Slugifier.withoutTransliteration(tr).apply("ISTANBUL"));

        System.out.println("-- compose vs. andThen");
        Function<String, String> tidy = Slugifier.trimDashes().compose(Slugifier.collapseDashes());
        System.out.println("trimDashes.compose(collapseDashes)(\"--a--b--\") = " + tidy.apply("--a--b--"));
    }
}
