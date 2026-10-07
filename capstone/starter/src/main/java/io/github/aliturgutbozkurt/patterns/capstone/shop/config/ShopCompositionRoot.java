package io.github.aliturgutbozkurt.patterns.capstone.shop.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopEnvironment;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;
import java.util.Objects;

/**
 * Your composition root: the only place where the acceptance tests create your shop. Build the domain, application
 * services and adapters here, wire them to the {@link ShopEnvironment}, and return them as one {@link PatternShop}.
 *
 * <p>The given acceptance tests call {@code new ShopCompositionRoot().create(env)} for every test, so a shop must never
 * share state with another shop (no static state).
 */
public final class ShopCompositionRoot implements PatternShopFactory {

    @Override
    public PatternShop create(ShopEnvironment env) {
        Objects.requireNonNull(env, "env");
        // TODO(capstone): create repositories, services and adapters from env and return them. Start with F1.
        return new PatternShop() {
            @Override
            public CatalogueUseCase catalogue() {
                throw new UnsupportedOperationException("TODO(capstone): catalogue use case (F1)");
            }

            @Override
            public CartUseCase carts() {
                throw new UnsupportedOperationException("TODO(capstone): cart use case (F2, F3)");
            }

            @Override
            public PricingUseCase pricing() {
                throw new UnsupportedOperationException("TODO(capstone): pricing use case (F4)");
            }

            @Override
            public CheckoutUseCase checkout() {
                throw new UnsupportedOperationException("TODO(capstone): checkout use case (F5, F6)");
            }

            @Override
            public OrderUseCase orders() {
                throw new UnsupportedOperationException("TODO(capstone): order use case (F7)");
            }

            @Override
            public ShopEvents events() {
                throw new UnsupportedOperationException("TODO(capstone): domain events (F8)");
            }

            @Override
            public FulfilmentUseCase fulfilment() {
                throw new UnsupportedOperationException("TODO(capstone): fulfilment use case (F9)");
            }

            @Override
            public ReportUseCase reports() {
                throw new UnsupportedOperationException("TODO(capstone): report use case (F10)");
            }

            @Override
            public CommandLine cli() {
                throw new UnsupportedOperationException("TODO(capstone): command-line adapter (F11)");
            }
        };
    }
}
