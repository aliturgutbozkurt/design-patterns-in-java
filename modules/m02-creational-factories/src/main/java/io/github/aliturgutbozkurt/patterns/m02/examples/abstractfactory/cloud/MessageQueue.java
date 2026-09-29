package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud;

import java.util.List;

/**
 * Abstract product 2: a message queue.
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public interface MessageQueue {

    String provider();

    void publish(String message);

    List<String> messages();
}
