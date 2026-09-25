package io.github.aliturgutbozkurt.patterns.m00.examples.functional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** Run: {@code java modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/functional/TextPipelineDemo.java} */
public final class TextPipelineDemo {

    private TextPipelineDemo() {}

    public static void main(String[] args) {
        String input = "  Design   PATTERNS\tin Java ";
        Function<String, String> normalize = TextPipeline.normalize();
        Function<String, String> shout = TextPipeline.compose(List.of(String::strip, text -> text + "!", String::toUpperCase));

        System.out.println("normalize: \"" + normalize.apply(input) + "\"");
        System.out.println("shout:     \"" + normalize.andThen(shout).apply(input) + "\"");

        var keyword = TextPipeline.isKeyword(Set.of("record", "sealed", "switch"));
        List<String> found = Arrays.stream("a record is sealed when you switch on it".split(" "))
                .filter(keyword)
                .toList();
        System.out.println("keywords:  " + found);
    }
}
