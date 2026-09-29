package io.github.aliturgutbozkurt.patterns.m01.exercises.ex02;

/** GIVEN — do not modify. Sends a message to a member (e-mail, SMS, … — the service must not care). */
@FunctionalInterface
public interface Notifier {

    void send(Member member, String message);
}
