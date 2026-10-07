package io.github.aliturgutbozkurt.patterns.capstone.reference.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;

/** The wired shop: a record whose accessors are exactly the {@link PatternShop} ports. */
record ReferenceShop(CatalogueUseCase catalogue, CartUseCase carts, PricingUseCase pricing, CheckoutUseCase checkout,
                     OrderUseCase orders, ShopEvents events, FulfilmentUseCase fulfilment, ReportUseCase reports,
                     CommandLine cli) implements PatternShop {
}
