package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking;

/**
 * Where documents are saved (disk, cloud). Writing can be slow, which is why an auto-save should not queue up
 * behind another one.
 *
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
@FunctionalInterface
public interface DocumentStore {

    void write(long version, String text);
}
