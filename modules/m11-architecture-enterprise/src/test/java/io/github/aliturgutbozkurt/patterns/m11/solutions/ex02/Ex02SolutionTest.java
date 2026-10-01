package io.github.aliturgutbozkurt.patterns.m11.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderLifecycle;
import io.github.aliturgutbozkurt.patterns.m11.exercises.ex02.OrderStore;
import java.util.function.Consumer;
import java.util.function.Supplier;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected OrderLifecycle newLifecycle(OrderStore store, Supplier<OrderId> ids,
                                          Consumer<RuntimeException> errorHandler) {
        return new OrderLifecycleService(store, ids, errorHandler);
    }
}
