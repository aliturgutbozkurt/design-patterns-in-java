package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductQuery;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductSpec;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductView;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.port.out.ProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.DigitalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.PhysicalProduct;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.ProductFactory;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.ProductSpecs;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Feature F1: the catalogue use case over the product repository.
 *
 * @see "capstone guide, Slice walkthrough — C3"
 */
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "application service behind an inbound port")
public final class CatalogueService implements CatalogueUseCase {

    private final ProductRepository products;
    private final UnitOfWork unitOfWork;

    public CatalogueService(ProductRepository products, UnitOfWork unitOfWork) {
        this.products = Objects.requireNonNull(products, "products");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
    }

    @Override
    public ProductView add(ProductSpec spec) {
        Product product = ProductFactory.forType(spec.type())
                .create(spec.sku(), spec.name(), spec.category(), spec.price(), spec.initialStock());
        return unitOfWork.run(_ -> {
            if (products.find(spec.sku()).isPresent()) {
                throw new IllegalArgumentException("duplicate SKU: " + spec.sku().value());
            }
            products.save(product);
            return Views.of(product);
        });
    }

    @Override
    public Optional<ProductView> find(Sku sku) {
        return products.find(sku).map(Views::of);
    }

    @Override
    public List<ProductView> search(ProductQuery query) {
        return products.findMatching(ProductSpecs.inAnyCategory(query.categories())
                        .and(ProductSpecs.priceAtMost(new Money(query.maxPriceKurus())))
                        .and(ProductSpecs.nameContains(query.nameContains())))
                .stream().map(Views::of).toList();
    }

    @Override
    public ProductView restock(Sku sku, int quantity) {
        return unitOfWork.run(_ -> {
            PhysicalProduct restocked = switch (products.find(sku).orElseThrow(() -> unknown(sku))) {
                case PhysicalProduct physical -> physical.restocked(quantity);
                case DigitalProduct _ -> throw new IllegalArgumentException(
                        "cannot restock a digital product: " + sku.value());
            };
            products.save(restocked);
            return Views.of(restocked);
        });
    }

    private static NoSuchElementException unknown(Sku sku) {
        return new NoSuchElementException("unknown product: " + sku.value());
    }
}
