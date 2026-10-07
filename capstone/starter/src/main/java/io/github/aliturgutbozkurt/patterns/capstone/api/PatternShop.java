package io.github.aliturgutbozkurt.patterns.capstone.api;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;

/**
 * GIVEN — do not modify. One running shop: its inbound ports, one per feature. All of them share the same state; every
 * call returns the same port object.
 *
 * @see "capstone brief §6 — What you are given"
 */
public interface PatternShop {

    /** F1 */
    CatalogueUseCase catalogue();

    /** F2, F3 */
    CartUseCase carts();

    /** F4 */
    PricingUseCase pricing();

    /** F5, F6 */
    CheckoutUseCase checkout();

    /** F6, F7 */
    OrderUseCase orders();

    /** F8 */
    ShopEvents events();

    /** F9 */
    FulfilmentUseCase fulfilment();

    /** F10 */
    ReportUseCase reports();

    /** F11 */
    CommandLine cli();
}
