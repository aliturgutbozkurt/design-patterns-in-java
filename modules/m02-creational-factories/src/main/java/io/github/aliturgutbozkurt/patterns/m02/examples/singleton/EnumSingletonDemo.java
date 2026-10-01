package io.github.aliturgutbozkurt.patterns.m02.examples.singleton;

import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.enumsingleton.AppSettings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

/**
 * Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/singleton/EnumSingletonDemo.java}
 *
 * @see "m02 lesson, section Singleton"
 */
public final class EnumSingletonDemo {

    private EnumSingletonDemo() {}

    public static void main(String[] args) {
        var settings = AppSettings.INSTANCE;
        System.out.println("app.name = " + settings.get("app.name"));
        System.out.println("page size = " + settings.pageSize());
        System.out.println("same instance via valueOf? " + (settings == AppSettings.valueOf("INSTANCE")));
        System.out.println("same instance after serialization? " + (settings == roundTrip(settings)));
    }

    /** Serializes and deserializes a value — for an enum the JVM hands back the existing constant. */
    private static Object roundTrip(Object value) {
        try {
            var bytes = new ByteArrayOutputStream();
            try (var out = new ObjectOutputStream(bytes)) {
                out.writeObject(value);
            }
            try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                return in.readObject();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
