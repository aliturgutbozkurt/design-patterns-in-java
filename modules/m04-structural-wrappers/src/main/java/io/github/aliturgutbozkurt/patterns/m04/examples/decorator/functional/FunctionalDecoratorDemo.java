package io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional;

import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.censor;
import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.collapseSpaces;
import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.trim;
import static io.github.aliturgutbozkurt.patterns.m04.examples.decorator.functional.TextFilters.truncate;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** Run: {@code java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/decorator/functional/FunctionalDecoratorDemo.java} */
public final class FunctionalDecoratorDemo {

    private FunctionalDecoratorDemo() {}

    public static void main(String[] args) {
        Set<String> banned = Set.of("darn");
        CommentPipeline moderation = CommentPipeline.of(List.of(
                trim(), collapseSpaces(), censor(banned), truncate(30)));      // built once ...

        String comment = "   This   darn  product is GREAT,   darn it!   ";
        System.out.println("before: [" + comment + "]");
        System.out.println("after:  [" + moderation.apply(comment) + "]");     // ... reused for every comment

        Function<String, String> censorThenTruncate = censor(banned).andThen(truncate(10));
        Function<String, String> truncateThenCensor = truncate(10).andThen(censor(banned));
        System.out.println("censor, then truncate(10): [" + censorThenTruncate.apply("this is darn good") + "]");
        System.out.println("truncate(10), then censor: [" + truncateThenCensor.apply("this is darn good") + "]");
    }
}
