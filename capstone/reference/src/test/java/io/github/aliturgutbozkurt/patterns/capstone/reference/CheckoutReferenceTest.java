package io.github.aliturgutbozkurt.patterns.capstone.reference;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.CheckoutAcceptance;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.config.ReferenceCompositionRoot;

class CheckoutReferenceTest extends CheckoutAcceptance {

    @Override
    protected PatternShopFactory factory() {
        return new ReferenceCompositionRoot();
    }

    @Override
    protected String applicationRootPackage() {
        return "io.github.aliturgutbozkurt.patterns.capstone.reference";
    }
}
