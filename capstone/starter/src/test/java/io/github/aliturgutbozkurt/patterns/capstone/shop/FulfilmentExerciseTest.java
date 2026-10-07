package io.github.aliturgutbozkurt.patterns.capstone.shop;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.FulfilmentAcceptance;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.shop.config.ShopCompositionRoot;
import org.junit.jupiter.api.Tag;

/** GIVEN — do not modify. Runs {@link FulfilmentAcceptance} against YOUR shop: {@code ./mvnw -pl capstone/starter test -Pexercises}. */
@Tag("exercise")
class FulfilmentExerciseTest extends FulfilmentAcceptance {

    @Override
    protected PatternShopFactory factory() {
        return new ShopCompositionRoot();
    }

    @Override
    protected String applicationRootPackage() {
        return "io.github.aliturgutbozkurt.patterns.capstone.shop";
    }
}
