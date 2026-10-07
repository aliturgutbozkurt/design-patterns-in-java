package io.github.aliturgutbozkurt.patterns.capstone.reference;

import io.github.aliturgutbozkurt.patterns.capstone.acceptance.ArchitectureRules;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.config.ReferenceCompositionRoot;

class ReferenceArchitectureTest extends ArchitectureRules {

    @Override
    protected PatternShopFactory factory() {
        return new ReferenceCompositionRoot();
    }

    @Override
    protected String applicationRootPackage() {
        return "io.github.aliturgutbozkurt.patterns.capstone.reference";
    }
}
