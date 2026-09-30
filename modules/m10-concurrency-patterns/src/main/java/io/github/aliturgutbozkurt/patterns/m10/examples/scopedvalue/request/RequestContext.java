package io.github.aliturgutbozkurt.patterns.m10.examples.scopedvalue.request;

/**
 * The implicit context of a request. A {@link ScopedValue} is an immutable key; the value is bound for the
 * duration of one {@code call}/{@code run} and unbound again afterwards. There is no setter and nothing to
 * {@code remove()}, so the constants are not mutable static state.
 *
 * @see "m10 lesson, section Scoped Values"
 */
public final class RequestContext {

    /** Who is calling. */
    public static final ScopedValue<Principal> PRINCIPAL = ScopedValue.newInstance();

    /** Correlation id of the current request. */
    public static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    private RequestContext() {}
}
