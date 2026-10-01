package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.config.ShopCompositionRoot;

class InMemoryShopTest extends ShopUseCaseContract {

    @Override
    protected ShopCompositionRoot newRoot() {
        return ShopCompositionRoot.inMemory();
    }
}
