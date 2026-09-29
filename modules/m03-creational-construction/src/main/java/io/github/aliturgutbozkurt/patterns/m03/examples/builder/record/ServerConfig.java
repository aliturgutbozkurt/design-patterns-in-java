package io.github.aliturgutbozkurt.patterns.m03.examples.builder.record;

import java.time.Duration;
import java.util.Objects;

/**
 * Record + nested builder: the record's compact constructor is the <em>single</em> place that validates, the builder
 * only supplies defaults and names, and {@code withX} methods create modified copies.
 *
 * @see "m03 lesson, section Builder — records"
 */
public record ServerConfig(String host, int port, Duration timeout, boolean tls, int maxConnections) {

    public ServerConfig {
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(timeout, "timeout");
        if (host.isBlank()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        if (port < 1 || port > 65_535) {
            throw new IllegalArgumentException("port must be in 1..65535: " + port);
        }
        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout must be positive: " + timeout);
        }
        if (maxConnections < 1 || maxConnections > 10_000) {
            throw new IllegalArgumentException("maxConnections must be in 1..10000: " + maxConnections);
        }
    }

    /** Starts a builder; the host is required, everything else has a default. */
    public static Builder builder(String host) {
        return new Builder(host);
    }

    public ServerConfig withPort(int newPort) {
        return new ServerConfig(host, newPort, timeout, tls, maxConnections);
    }

    public ServerConfig withTls(boolean newTls) {
        return new ServerConfig(host, port, timeout, newTls, maxConnections);
    }

    /**
     * Defaults: port 8080, timeout 30 s, no TLS, 100 connections.
     *
     * @see "m03 lesson, section Builder — records"
     */
    public static final class Builder {

        private final String host;
        private int port = 8080;
        private Duration timeout = Duration.ofSeconds(30);
        private boolean tls;
        private int maxConnections = 100;

        private Builder(String host) {
            this.host = host;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder tls(boolean tls) {
            this.tls = tls;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        /** The record's compact constructor validates. */
        public ServerConfig build() {
            return new ServerConfig(host, port, timeout, tls, maxConnections);
        }
    }
}
