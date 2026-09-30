package io.github.aliturgutbozkurt.patterns.m05.examples.bridge.remote;

/**
 * Bridge implementor, classic GoF form: the low-level operations every device offers. Remotes are built only on
 * these primitives, so remotes and devices can each grow without touching the other hierarchy.
 *
 * @see "m05 lesson, section Bridge"
 */
public interface Device {

    boolean isEnabled();

    void enable();

    void disable();

    /** 0..100. */
    int volume();

    /** @throws IllegalArgumentException outside 0..100 */
    void setVolume(int volume);

    /** 1..{@link #channelCount()}. */
    int channel();

    /** @throws IllegalArgumentException outside 1..{@link #channelCount()} */
    void setChannel(int channel);

    int channelCount();

    /** One line describing the current state. */
    String status();
}
