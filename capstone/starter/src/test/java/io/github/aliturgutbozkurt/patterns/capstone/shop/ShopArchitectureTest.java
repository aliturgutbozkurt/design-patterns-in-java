package io.github.aliturgutbozkurt.patterns.capstone.shop;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.ArchitectureRules;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.shop.config.ShopCompositionRoot;

/**
 * GIVEN — do not modify. The seven architecture rules on YOUR code; runs in every build ({@code ./mvnw -pl
 * capstone/starter verify}) and must stay green.
 */
class ShopArchitectureTest extends ArchitectureRules {

    @Override
    protected PatternShopFactory factory() {
        return new ShopCompositionRoot();
    }

    @Override
    protected String applicationRootPackage() {
        return "io.github.aliturgutbozkurt.patterns.capstone.shop";
    }
}
