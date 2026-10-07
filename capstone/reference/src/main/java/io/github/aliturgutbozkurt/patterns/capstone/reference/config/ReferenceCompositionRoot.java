package io.github.aliturgutbozkurt.patterns.capstone.reference.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopEnvironment;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryCartRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryProductRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.out.memory.InMemoryPromotionRepository;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.CartService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.CatalogueService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.PricingService;
import io.github.aliturgutbozkurt.patterns.capstone.reference.application.events.UnitOfWork;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.ids.SequentialIds;
import java.util.Objects;

/**
 * The composition root: the only place that knows every concrete class. It creates one object graph per shop (no
 * static state), wires the services to the outbound adapters and the environment, and returns the inbound ports.
 *
 * @see "capstone guide §1 Pattern map — architectural patterns"
 */
@PatternRole(value = DesignPattern.DEPENDENCY_INJECTION, role = "composition root")
public final class ReferenceCompositionRoot implements PatternShopFactory {

    @Override
    public PatternShop create(ShopEnvironment env) {
        Objects.requireNonNull(env, "env");
        var products = new InMemoryProductRepository();
        var carts = new InMemoryCartRepository();
        var promotions = new InMemoryPromotionRepository();
        var unitOfWork = new UnitOfWork();

        return new ReferenceShop(
                new CatalogueService(products, unitOfWork),
                new CartService(carts, products, promotions, SequentialIds.forCarts(), env.clock(), unitOfWork),
                new PricingService(promotions, carts, products, env.clock()),
                PendingSlices.checkout(),
                PendingSlices.orders(),
                PendingSlices.events(),
                PendingSlices.fulfilment(),
                PendingSlices.reports(),
                PendingSlices.cli());
    }
}
