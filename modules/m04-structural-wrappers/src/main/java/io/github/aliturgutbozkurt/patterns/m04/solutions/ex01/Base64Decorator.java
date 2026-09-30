package io.github.aliturgutbozkurt.patterns.m04.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m04.exercises.ex01.DataSource;
import java.util.Base64;

/**
 * Reference solution for assignment 01: stores standard Base64 text (US-ASCII bytes).
 *
 * @see "m04 lesson, section Decorator"
 */
public class Base64Decorator extends DataSourceDecorator {

    public Base64Decorator(DataSource wrapped) {
        super(wrapped);
    }

    @Override
    protected byte[] encode(byte[] data) {
        return Base64.getEncoder().encode(data);               // Base64 output is plain ASCII
    }

    @Override
    protected byte[] decode(byte[] stored) {
        try {
            return Base64.getDecoder().decode(stored);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("corrupt Base64 data", e);
        }
    }
}
