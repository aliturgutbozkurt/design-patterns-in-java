package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.caching;

import java.math.BigDecimal;

/**
 * Subject: how many units of {@code to} one unit of {@code from} buys.
 *
 * @see "m04 lesson, section Proxy"
 */
public interface ExchangeRateService {

    BigDecimal rate(String from, String to);
}
