package io.github.aliturgutbozkurt.patterns.m11.examples.repository;

import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.InMemoryProductRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.repository.catalog.ProductRepository;

class InMemoryProductRepositoryTest extends ProductRepositoryContract {

    @Override
    protected ProductRepository newRepository() {
        return new InMemoryProductRepository();
    }
}
