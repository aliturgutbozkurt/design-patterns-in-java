package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment;

import java.util.List;

/** What moves between two stages: an order with its stage log so far, or the poison pill. */
sealed interface Parcel {

    record InTransit(Order order, List<String> stageLog) implements Parcel {
        public InTransit {
            stageLog = List.copyOf(stageLog);
        }
    }

    record EndOfOrders() implements Parcel {}
}
