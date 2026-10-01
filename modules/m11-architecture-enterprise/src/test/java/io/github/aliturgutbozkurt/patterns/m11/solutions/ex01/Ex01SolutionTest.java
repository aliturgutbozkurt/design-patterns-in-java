package io.github.aliturgutbozkurt.patterns.m11.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.EventPublisher;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.Ex01Contract;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderIdGenerator;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.PaymentPort;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.ProductCatalog;
import io.github.aliturgutbozkurt.patterns.m11.solutions.ex01.adapter.InMemoryOrderRepository;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected CheckoutUseCase newService(ProductCatalog catalog, PaymentPort payments, OrderRepository orders,
                                         EventPublisher events, OrderIdGenerator ids) {
        return new CheckoutService(catalog, payments, orders, events, ids);
    }

    @Override
    protected OrderRepository newRepository() {
        return new InMemoryOrderRepository();
    }
}
