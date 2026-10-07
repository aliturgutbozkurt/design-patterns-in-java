package io.github.aliturgutbozkurt.patterns.capstone.reference.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvent;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.ShopEvents;
import io.github.aliturgutbozkurt.patterns.capstone.api.event.Subscription;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderView;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.TransitionResult;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportFormat;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/** Ports of the slices that are not built yet (C4–C6); each call says which slice brings it. Removed in C6. */
final class PendingSlices {

    private PendingSlices() {
    }

    private static UnsupportedOperationException pending(String what, String slice) {
        return new UnsupportedOperationException(what + " arrives in " + slice);
    }

    static CheckoutUseCase checkout() {
        return _ -> {
            throw pending("checkout", "C5");
        };
    }

    static OrderUseCase orders() {
        return new OrderUseCase() {
            @Override
            public Optional<OrderView> find(OrderId order) {
                throw pending("orders", "C5");
            }

            @Override
            public List<OrderView> ordersOf(CustomerId customer) {
                throw pending("orders", "C5");
            }

            @Override
            public TransitionResult cancel(OrderId order, String reason) {
                throw pending("orders", "C5");
            }

            @Override
            public TransitionResult markDelivered(OrderId order) {
                throw pending("orders", "C5");
            }
        };
    }

    static ShopEvents events() {
        return new ShopEvents() {
            @Override
            public <E extends ShopEvent> Subscription subscribe(Class<E> type, Consumer<? super E> handler) {
                throw pending("events", "C5");
            }
        };
    }

    static FulfilmentUseCase fulfilment() {
        return () -> {
            throw pending("fulfilment", "C6");
        };
    }

    static ReportUseCase reports() {
        return new ReportUseCase() {
            @Override
            public Report run(ReportRequest request) {
                throw pending("reports", "C6");
            }

            @Override
            public String render(Report report, ReportFormat format) {
                throw pending("reports", "C6");
            }
        };
    }

    static CommandLine cli() {
        return _ -> {
            throw pending("the CLI", "C6");
        };
    }
}
