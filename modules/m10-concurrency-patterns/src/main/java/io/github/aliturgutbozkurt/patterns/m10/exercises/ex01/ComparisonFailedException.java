package io.github.aliturgutbozkurt.patterns.m10.exercises.ex01;

/** GIVEN — do not modify. Thrown under {@link FailurePolicy#FAIL_FAST}: names the provider that failed first. */
public final class ComparisonFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String provider;

    public ComparisonFailedException(String provider, Throwable cause) {
        super("provider " + provider + " failed", cause);
        this.provider = provider;
    }

    public String provider() {
        return provider;
    }
}
