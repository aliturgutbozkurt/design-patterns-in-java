package io.github.aliturgutbozkurt.patterns.m09.examples.composition.pricing;

import java.util.List;
import java.util.function.Function;

/**
 * Folds a list of rules into one function, applied in list order. An empty list gives the identity.
 *
 * @see "m09 lesson, section Function composition, currying and partial application"
 */
public final class PricePipeline {

    private PricePipeline() {}

    public static Function<Price, Price> of(List<Function<Price, Price>> rules) {
        return rules.stream().reduce(Function.identity(), Function::andThen);
    }
}
