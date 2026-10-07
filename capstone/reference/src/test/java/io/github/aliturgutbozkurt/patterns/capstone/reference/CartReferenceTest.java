package io.github.aliturgutbozkurt.patterns.capstone.reference;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.CartAcceptance;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.config.ReferenceCompositionRoot;

class CartReferenceTest extends CartAcceptance {

    @Override
    protected PatternShopFactory factory() {
        return new ReferenceCompositionRoot();
    }

    @Override
    protected String applicationRootPackage() {
        return "io.github.aliturgutbozkurt.patterns.capstone.reference";
    }
}
