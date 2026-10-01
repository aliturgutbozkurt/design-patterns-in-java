package io.github.aliturgutbozkurt.patterns.m11.exercises.ex01;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex01.adapter.InMemoryOrderRepository;
import org.junit.jupiter.api.Tag;

/** Runs the contract against YOUR code: {@code ./mvnw -pl modules/m11-architecture-enterprise test -Pexercises}. */
@Tag("exercise")
class Ex01ExerciseTest extends Ex01Contract {

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
