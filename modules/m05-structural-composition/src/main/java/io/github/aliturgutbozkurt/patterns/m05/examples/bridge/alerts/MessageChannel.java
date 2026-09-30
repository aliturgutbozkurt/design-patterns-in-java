package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.alerts;

/**
 * Bridge implementor with a single method, so any lambda is a channel: <em>where</em> a message goes.
 *
 * @see "m05 lesson, section Bridge — modern Java 27"
 */
@FunctionalInterface
public interface MessageChannel {

    void send(String message);
}
