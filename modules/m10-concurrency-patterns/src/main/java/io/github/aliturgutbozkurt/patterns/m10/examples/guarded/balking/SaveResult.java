package io.github.aliturgutbozkurt.patterns.m10.examples.guarded.balking;

/**
 * What {@link AutoSavingDocument#save()} did. Two of the three answers are "balked": the call returned at once
 * because its precondition did not hold, instead of waiting for it.
 *
 * @see "m10 lesson, section Guarded Suspension and Balking"
 */
public enum SaveResult {
    /** The current text was written to the store. */
    SAVED,
    /** Balked: nothing changed since the last save. */
    SKIPPED_CLEAN,
    /** Balked: another save is running right now. */
    SKIPPED_IN_PROGRESS
}
