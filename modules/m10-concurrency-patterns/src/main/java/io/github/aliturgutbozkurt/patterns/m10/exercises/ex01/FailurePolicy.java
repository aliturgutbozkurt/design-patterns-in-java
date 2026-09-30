package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

/** GIVEN — do not modify. What a provider exception means for the whole comparison. */
public enum FailurePolicy {
    /** Record the failure as {@link ProviderResult.Failed} and keep the other results. */
    BEST_EFFORT,
    /** Cancel every provider still running and throw {@link ComparisonFailedException} at once. */
    FAIL_FAST
}
