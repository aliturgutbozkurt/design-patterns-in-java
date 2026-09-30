package io.github.aliturgutbozkurt.patterns.m11.examples.testdoubles.time;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Login sessions that expire after {@code ttl} without activity. Time and token generation are injected, so expiry
 * is testable to the millisecond without {@code Thread.sleep}. Rule: a session is expired when
 * {@code now ≥ lastActivity + ttl}.
 *
 * @see "m11 lesson, section Test doubles — time"
 */
public final class SessionManager {

    private record Session(String user, Instant lastActivity) {}

    private final Clock clock;
    private final Duration ttl;
    private final Supplier<String> tokens;
    private final Map<String, Session> sessions = new HashMap<>();

    public SessionManager(Clock clock, Duration ttl, Supplier<String> tokens) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.ttl = Objects.requireNonNull(ttl, "ttl");
        this.tokens = Objects.requireNonNull(tokens, "tokens");
    }

    /** Starts a session and returns its token. */
    public String login(String user) {
        String token = tokens.get();
        sessions.put(token, new Session(Objects.requireNonNull(user, "user"), clock.instant()));
        return token;
    }

    public boolean isValid(String token) {
        Session session = sessions.get(token);
        return session != null && clock.instant().isBefore(session.lastActivity().plus(ttl));
    }

    /** Records activity: the session lives another {@code ttl} from now. */
    public void touch(String token) {
        if (!isValid(token)) {
            throw new IllegalStateException("session expired: " + token);
        }
        sessions.put(token, new Session(sessions.get(token).user(), clock.instant()));
    }

    /** The user of a valid session. */
    public Optional<String> userOf(String token) {
        return isValid(token) ? Optional.of(sessions.get(token).user()) : Optional.empty();
    }
}
