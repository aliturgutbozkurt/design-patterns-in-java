package io.github.aliturgutbozkurt.patterns.capstone.reference.application;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartView;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.ProductView;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.StatusChange;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.cart.Cart;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.catalogue.Product;
import io.github.aliturgutbozkurt.patterns.capstone.reference.domain.order.Order;

/**
 * Maps domain objects to the GIVEN views the inbound ports return.
 *
 * @see "capstone guide, Slice walkthrough — C3"
 */
public final class Views {

    private Views() {
    }

    /** The catalogue's view of a product. */
    public static ProductView of(Product product) {
        return new ProductView(product.sku(), product.name(), product.category(), product.type(), product.price(),
                product.stock());
    }

    /** The view of a cart. */
    public static CartView of(Cart cart) {
        return new CartView(cart.id(), cart.customer(),
                cart.items().stream().map(item -> new CartLine(item.sku(), item.quantity())).toList(),
                cart.coupon(), cart.open());
    }

    /** The view of an order. */
    public static OrderView of(Order order) {
        return new OrderView(order.id(), order.customer(),
                order.items().stream().map(item -> new OrderLine(item.sku(), item.name(), item.type(),
                        item.quantity(), item.unitPrice())).toList(),
                order.total(), order.status(), order.state().paymentReference(), order.state().trackingCode(),
                order.placedAt(),
                order.history().stream().map(entry -> new StatusChange(entry.status(), entry.at(), entry.note()))
                        .toList());
    }
}
