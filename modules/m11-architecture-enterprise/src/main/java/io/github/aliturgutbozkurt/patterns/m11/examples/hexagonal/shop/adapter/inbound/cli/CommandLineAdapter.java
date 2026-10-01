package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.inbound.cli;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand.LineRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Placed;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderUseCase;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Inbound adapter: {@code place alice BOOK-1:2 PEN-7:1} in, {@code PLACED order-1 total 47.00} out. Parsing and
 * printing only — every business decision is behind the {@link PlaceOrderUseCase} port.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class CommandLineAdapter {

    static final String USAGE = "usage: place <customer> <sku>:<quantity>...";

    private final PlaceOrderUseCase placeOrder;

    public CommandLineAdapter(PlaceOrderUseCase placeOrder) {
        this.placeOrder = Objects.requireNonNull(placeOrder, "placeOrder");
    }

    /** Handles one command line and returns the line to print. */
    public String handle(String line) {
        String[] words = line.strip().split("\\s+");
        if (words.length < 3 || !words[0].equals("place")) {
            return USAGE;
        }
        List<LineRequest> lines = new ArrayList<>();
        for (String item : Arrays.asList(words).subList(2, words.length)) {
            String[] parts = item.split(":");
            if (parts.length != 2) {
                return USAGE;
            }
            try {
                lines.add(new LineRequest(new Sku(parts[0]), Integer.parseInt(parts[1])));
            } catch (IllegalArgumentException _) { // bad SKU or quantity text: a usage error, not a crash
                return USAGE;
            }
        }
        return switch (placeOrder.place(new PlaceOrderCommand(words[1], lines))) {
            case Placed(OrderId id, Money total) -> "PLACED " + id + " total " + total;
            case Rejected(String reason) -> "REJECTED " + reason;
        };
    }
}
