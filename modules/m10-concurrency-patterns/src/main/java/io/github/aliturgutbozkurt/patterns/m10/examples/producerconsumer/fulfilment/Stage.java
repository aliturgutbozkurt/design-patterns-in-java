package io.github.aliturgutbozkurt.patterns.m10.examples.producerconsumer.fulfilment;

/**
 * The work one warehouse stage does on an order. It may block (a slow packer, a courier that is late); while it
 * does, orders pile up in the stage's input queue until the queue is full.
 *
 * @see "m10 lesson, section Producer–Consumer"
 */
@FunctionalInterface
public interface Stage {

    void process(Order order) throws InterruptedException;
}
