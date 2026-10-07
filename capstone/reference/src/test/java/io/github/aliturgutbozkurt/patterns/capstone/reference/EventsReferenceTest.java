package io.github.aliturgutbozkurt.patterns.capstone.reference;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.EventsAcceptance;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.config.ReferenceCompositionRoot;

class EventsReferenceTest extends EventsAcceptance {

    @Override
    protected PatternShopFactory factory() {
        return new ReferenceCompositionRoot();
    }

    @Override
    protected String applicationRootPackage() {
        return "io.github.aliturgutbozkurt.patterns.capstone.reference";
    }
}
