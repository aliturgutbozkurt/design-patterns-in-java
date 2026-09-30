package io.github.aliturgutbozkurt.patterns.m07.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Auction;
import io.github.aliturgutbozkurt.patterns.m07.exercises.ex01.Ex01Contract;
import java.util.function.Consumer;

class Ex01SolutionTest extends Ex01Contract {

    @Override
    protected Auction newAuction(
            long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler) {
        return new LiveAuction(startingPriceCents, minIncrementCents, errorHandler);
    }
}
