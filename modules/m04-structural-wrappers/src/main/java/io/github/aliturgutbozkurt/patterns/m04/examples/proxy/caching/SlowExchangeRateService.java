package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.caching;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Fake remote service: answers from a fixed, fictional rate table and counts every call it would have to pay for.
 *
 * @see "m04 lesson, section Proxy"
 */
public final class SlowExchangeRateService implements ExchangeRateService {

    private final Map<String, BigDecimal> rates;
    private int calls;

    public SlowExchangeRateService(Map<String, BigDecimal> rates) {
        this.rates = Map.copyOf(rates);
    }

    @Override
    public BigDecimal rate(String from, String to) {
        calls++;
        BigDecimal rate = rates.get(from + "/" + to);
        if (rate == null) {
            throw new IllegalArgumentException("no rate for " + from + "/" + to);
        }
        return rate;
    }

    public int calls() {
        return calls;
    }
}
