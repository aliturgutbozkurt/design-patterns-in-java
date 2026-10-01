package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment;

import java.util.List;

/**
 * An order that left the last stage, with the stages it went through in order.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
public record Shipment(long orderId, List<String> stageLog) {

    public Shipment {
        stageLog = List.copyOf(stageLog);
    }
}
